package agents;

import java.io.IOException;
import java.io.Serializable;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class BaseAgent extends Agent {
	
	protected void registerDfAgent(String platform, String type, String idType)
	{
		DFAgentDescription ad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName("Platform-"+platform);
		sd.setType(type);
		
		ServiceDescription sd1 = new ServiceDescription();
		sd1.setName("Platform-"+platform);
		sd1.setType(idType);
		
		ad.addServices(sd1);
		ad.addServices(sd);
		
		try{
			DFService.register(this, ad);
		}catch(FIPAException e){
			e.printStackTrace();
		}
	}
	
	protected void registerDfAgent(String platform, String type)
	{
		DFAgentDescription ad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName("Platform-"+platform);
		sd.setType(type);
		ad.addServices(sd);
		
		try{
			DFService.register(this, ad);
		}catch(FIPAException e){
			e.printStackTrace();
		}
	}
	
	public Boolean sendMessageToAgentsByServiceType (Agent myAgent, String serviceType, 
			String conversationId, Serializable messageData)
	{
		try {
			DFAgentDescription[] agents = getAgentsbyServiceType(myAgent, serviceType);
			for(int i=0; i<agents.length; i++)
			{
				ACLMessage message = new ACLMessage(ACLMessage.INFORM);
				message.setContentObject(messageData);
				message.addReceiver(agents[i].getName()); 
				message.setConversationId(conversationId);
				myAgent.send(message);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public DFAgentDescription[] getAgentsbyServiceType (Agent myAgent, String serviceType)
	{
		DFAgentDescription ad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType(serviceType);
		ad.addServices(sd);
		DFAgentDescription[] ca = null;
		try {
			ca = DFService.search(myAgent, ad);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
		return ca;
	}
	
	
	protected void takeDown()
	{
		if (this.getDefaultDF() == null)
		{
			try {
				DFService.deregister(this);
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				DFService.deregister(this, this.getDefaultDF());
			} catch (FIPAException e) {
				e.printStackTrace();
			}
		}
	}

}
