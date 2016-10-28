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
		
		Connection conn = new DbConnection().getConnection();
		Statement stmt;
		try {
			System.out.println("ENTEREEEEED");
			stmt = conn.createStatement();
			conn.setAutoCommit(false);
			new DbAggregatorLoad().deleteOldForecastDataByIdLoad(this.myAgent.getName(), msgData.get(0).getIdAgent(), stmt);
			new DbAggregatorLoad().addFlexibilityLoadMessage(this.myAgent.getName(), msgData, stmt);
			//check if UpperLimit of all agents in all hours < Threshold

			if(checkThreshold(stmt)){
				if(msgData.get(0).getDesideredChoice() == msgData.get(0).getUpperLimit())
				{
					//aggiungo nel db i picchi che poi dovrò ignorare nei controlli futuri
					new DbPeakData().addPeaks(peaks, stmt);
					System.out.println("\n\nadd this fucking peak pls\n");
				}
				conn.commit();
				//new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
				//		this.myAgent.getAID(shortName), "permission", "ok");
			}
			else if(!checkThreshold(stmt) && msgData.get(0).getDesideredChoice() != msgData.get(0).getUpperLimit())
			{ //nel caso fossero uguali quell'agente non può fare niente, consuma già il massimo
				System.out.println("I'm sleeping!");
				
				conn.rollback();
				//send message back to the agent with the data timepowerprice
				ArrayList<TimePowerPrice> data = new DbPriceData().getDailyTimePowerPrice(msgData.get(0).getDatetime());
				LoadInfo loadInfo = new DbLoadInfo().getLoadInfoByIdLoad(msgData.get(0).getIdAgent(), msgData.get(0).getDatetime());
				String shortName = new BaseAgent().getShortName(loadInfo.getIdAgent());
				new BaseAgent().sendMessageToAgentsByServiceType(this.myAgent, 
						this.myAgent.getAID(shortName), "permission", data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		int messagesReceived = new DbAggregatorLoad().countMessagesReceived(this.myAgent.getName(), msgData.get(0).getDatetime());
		int loadAgents = new BaseAgent().getAgentsbyServiceType(this.myAgent, "LoadAgent").length;
		
		System.out.println("LOAD messagesReceived: "+messagesReceived+" loadAgents: "+loadAgents);
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
	
	private Boolean checkThreshold(Statement stmt)
	{
		ArrayList<TimePowerPrice> list = new DbAggregatorLoad().checkThreshold(this.myAgent.getName(), 
				msgData.get(0).getDatetime(), stmt);

		//prendo i picchi esistenti nel db, se trova solo questi picchi che non si possono eliminare allora return true
		ArrayList<PeakData> existingPeaks = new DbPeakData().getTodayPeaks(this.myAgent.getName(), msgData.get(0).getDatetime(), stmt);
		
		for(int i=0; i<list.size(); i++)
		{
			double productionAvg = new DbDerData().getAverageLastMonthProduction(list.get(i).getDatetime());
			//EnergyPrice is the upperLimit, i'm sorry
			if(list.get(i).getEnergyPrice() > list.get(i).getThreshold()+productionAvg && !existingPeaks.contains(list.get(i))){
				PeakData data = new PeakData(this.myAgent.getName(), list.get(i).getDatetime(), list.get(i).getEnergyPrice());
				PeakData ciao = new PeakData("asd", list.get(i).getDatetime(), 10);
				peaks.add(data);
				peaks.add(ciao);
				System.out.println("list.get(i).getEnergyPrice(): "+list.get(i).getEnergyPrice()+" > "+list.get(i).getThreshold()+" + "+productionAvg+"?? SI!! "+list.get(i).getDatetime().getTime());
				return false;
			}
		}
		System.out.println("peaks size: "+peaks.size());
		for(int i=0; i<peaks.size(); i++) System.out.println("PEAK: "+peaks.get(i).getDatetime().getTime()+" - "+peaks.get(i).getPeakValue());
		return true;
	}
}
