package agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BaseAgent extends Agent {
	
	
	protected void registerDfAgent(String platform, String type, String idType)
	{
		DFAgentDescription ad = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setName("Platform-"+platform);
		sd.setType(type);
		if(!idType.equals(null))
		{
			ServiceDescription sd1 = new ServiceDescription();
			sd1.setName("Platform-"+idType);
			sd1.setType(idType);
			ad.addServices(sd1);
		}
		ad.addServices(sd);
		
		try{
			DFService.register(this, ad);
		}catch(FIPAException e){
			e.printStackTrace();
		}
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
