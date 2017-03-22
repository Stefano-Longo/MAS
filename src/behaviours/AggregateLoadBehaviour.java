package behaviours;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import agents.BaseAgent;
import basicData.FlexibilityData;
import basicData.LoadInfo;
import basicData.PeakData;
import basicData.TimePowerPrice;
import database.DbAggregatorLoad;
import database.DbConnection;
import database.DbDerData;
import database.DbLoadInfo;
import database.DbPeakData;
import database.DbPriceData;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

@SuppressWarnings("serial")
public class AggregateLoadBehaviour extends OneShotBehaviour {
	
	ACLMessage msg;
	ArrayList<FlexibilityData> msgData;
	ArrayList<PeakData> peaks = new ArrayList<PeakData>();
	@SuppressWarnings("unchecked")
	public AggregateLoadBehaviour(ACLMessage msg) 
	{
		this.msg = msg;
		try {
			msgData = (ArrayList<FlexibilityData>)msg.getContentObject();
		} catch (UnreadableException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void action() 
	{
		
		/**
		 * Controllo se aggiungendo questi dati non vado oltre. Oppure faccio tutto e alla fine controllo 
		 * e annullo la transaction in caso negativo
		 */
		
		/**
		 * Salvo i dati che mi vengono inviati nella stessa tabella LoadAggregatorData aggiungendo un campo
		 * booleano FORECAST. I test per il controllo del limite verranno effettuati sui dati FORECAST.
		 * Se l'upper limit futuro previsto va oltre la soglia allora si manda un messaggio al load agent 
		 * fargli cambiare lo shifting.
		 * 
		 * Appena arriva il messaggio dal load prima si eliminano i suoi dati vecchi e si aggiorna la tabella
		 * con i suoi dati nuovi. Poi si fa il controllo aggregando i dati. Se tutto va bene si committa altrimenti
		 * si fa il rollback e si manda un messaggio all'ultimo agente.
		 */
		
		DbConnection connection = new DbConnection();
		try {
			connection.stmt = connection.conn.createStatement();
			connection.conn.setAutoCommit(false);
			
			ArrayList<TimePowerPrice> list = new DbAggregatorLoad().checkThreshold(this.myAgent.getName(), 
					msgData.get(0).getDatetime(), connection.stmt);
			/*for(int i=0; i<list.size(); i++)
			{
				System.out.println(list.get(i).getDatetime().getTime()+" --> "+list.get(i).getEnergyPrice());
			}*/
			
			new DbAggregatorLoad().deleteOldDataByIdLoad(this.myAgent.getName(), msgData.get(0).getIdAgent(), msgData.get(0).getDatetime(), connection.stmt);
			//new DbAggregatorLoad().deleteOldForecastDataByIdLoad(this.myAgent.getName(), msgData.get(0).getIdAgent(), connection.stmt);
			new DbAggregatorLoad().addFlexibilityLoadMessage(this.myAgent.getName(), msgData, connection.stmt);
			//check if UpperLimit of all agents in all hours < Threshold
			//System.out.println("peaks size: "+peaks.size());
			/*for(int i=0; i<peaks.size(); i++) 
				System.out.println("PEAK: "+peaks.get(i).getDatetime().getTime()+" - "+peaks.get(i).getPeakValue());
			*/
			boolean existPeak = existPeak(connection.stmt);
			if(existPeak && msgData.get(0).getDesiredChoice() != msgData.get(0).getUpperLimit())
			{ 	//nuovo picco eliminabile
				//nel caso fossero uguali quell'agente non può fare niente, perché non shifta lì
				//System.out.println("Entered 1!");
				
				connection.conn.rollback();
				//send message back to the agent with the data timepowerprice
				ArrayList<TimePowerPrice> data = new DbPriceData().getDailyTimePowerPrice(msgData.get(0).getDatetime());
				LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdLoad(msgData.get(0).getIdAgent(), msgData.get(0).getDatetime());
				String shortName = new BaseAgent().getShortName(loadInfo.getIdAgent());
				new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
						this.myAgent.getAID(shortName), "permission", data);
				
			}else if(existPeak){//nuovi picchi non eliminabili
				//System.out.println("Entered 2!");
				connection.conn.commit();
				//System.out.println("\n Aggiungo i picchi da ignorare:"+peaks.size()+"\n");
				if(msgData.get(0).getDesiredChoice() == msgData.get(0).getUpperLimit())
				{
					//aggiungo nel db i picchi che poi dovrò ignorare nei controlli futuri
					new DbPeakData().addPeaks(peaks);
				}
			}else
			{ //no picchi
				//System.out.println("Entered 3!");
				connection.conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			connection.connClose();
		}
		
		
		int messagesReceived = new DbAggregatorLoad().countMessagesReceived(this.myAgent.getName(), msgData.get(0).getDatetime());
		int loadAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "LoadAgent").length;
		
		//System.out.println("LOAD messagesReceived: "+messagesReceived+" loadAgents: "+loadAgents);
		if (messagesReceived == loadAgents)
		{
			/**
			 * I have all the messages that I was waiting for so now I can
			 * send the message to ControlAgent
			 */

			FlexibilityData result = new DbAggregatorLoad().
					aggregateMessagesReceived(this.myAgent.getName(), msgData.get(0).getDatetime());

			new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, "ControlAgent",
					"proposal", result);
		}
	}
	
	private boolean existPeak(Statement stmt)
	{
		ArrayList<TimePowerPrice> list = new DbAggregatorLoad().checkThreshold(this.myAgent.getName(), 
				msgData.get(0).getDatetime(), stmt);
		/*for(int i=0; i<list.size(); i++)
		{
			System.out.println(list.get(i).getDatetime().getTime()+" -> "+list.get(i).getEnergyPrice());
		}*/
		
		//prendo i picchi esistenti nel db, se trova solo questi picchi che non si possono eliminare allora return true
		ArrayList<PeakData> existingPeaks = new DbPeakData().getTodayPeaks(this.myAgent.getName(), msgData.get(0).getDatetime());
		//stampa existingPeaks
		Boolean found = false;
		Boolean entered = false;
		int counter = 0;
		for(int i=0; i<list.size(); i++)
		{
			Boolean samedata = false;
			double productionAvg = new DbDerData().getAverageLastMonthProduction(list.get(i).getDatetime());
			//EnergyPrice is the lowerLimit, i'm sorry
			if(list.get(i).getEnergyPrice() > list.get(i).getThreshold()+productionAvg){
				counter++;//System.out.println(counter);

				//System.out.println("This is a PEAK: "+list.get(i).getDatetime().getTime()+" "+list.get(i).getEnergyPrice()+" > "+(list.get(i).getThreshold()+productionAvg));
					
				for(int k=0; k<existingPeaks.size(); k++) //controllo se è un picco già conosciuto
				{	//se ha stessa data e stesso valore di picco
					if(existingPeaks.get(k).getDatetime().getTime().compareTo(list.get(i).getDatetime().getTime()) == 0)
					{	//data uguale -> picco vecchio ma controlla se il valore è uguale per non far peggiorare il picco
						entered = true;
						//System.out.println("Already exist!");
						if(existingPeaks.get(k).getPeakValue() != list.get(i).getEnergyPrice())
						{
							//System.out.println(existingPeaks.get(k).getPeakValue()+" != "+list.get(i).getEnergyPrice());
							//System.out.println("And is not the same");
							samedata = true;
						}
					}
				}
				
				PeakData data = new PeakData(this.myAgent.getName(), list.get(i).getDatetime(), list.get(i).getEnergyPrice());
				if(!entered) //la data del nuovo picco è nuova -> nuovo picco 
				{
					//System.out.println("Already exists? NO");
					//System.out.println("Add");
					peaks.add(data);
					found = true;
				}
				else if(entered && samedata && msgData.get(0).getDesiredChoice() == msgData.get(0).getUpperLimit()) //fare l'update
				{
					PeakData peak = new PeakData(this.myAgent.getName(), list.get(i).getDatetime(), list.get(i).getEnergyPrice());
					new DbPeakData().updatePeak(peak);
				}
			}
		}
		return found;
	}
}
