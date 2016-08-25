package utils;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/**
 * Applicazione esterna che gestisce il lancio di una piattaforma ad agenti,
 * attraverso la creazione di un main container e la creazione di un'istanza di Agent0
 *
 */
public class PlatformCreator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/**
		 * Recupero dell'istanza della classe Runtime
		 */
		Runtime rt = Runtime.instance();
		
		/**
		 * Creazione di un main container:
		 * - il Profile contiene eventuali parametri di configurazione
		 * - createMainContainer(p) crea il main container della piattaforma
		 */
		Profile p = new ProfileImpl();
		ContainerController cc = rt.createMainContainer(p);
		
		try {
			AgentController tso = cc.createNewAgent("Tsololol", "agents.TsoAgent", null);
			/*	AgentController grid = cc.createNewAgent("GridAgent", "agents.GridAgent", null);
			AgentController control = cc.createNewAgent("ControlAgent", "agents.GridAgent", null);
			AgentController batteryAggregator = cc.createNewAgent("BatteryAggregator", "agents.GridAgent", null);
			AgentController derAggregator = cc.createNewAgent("DerAggregator", "agents.GridAgent", null);
			AgentController loadAggregator = cc.createNewAgent("LoadAggregator", "agents.GridAgent", null);
			AgentController battery = cc.createNewAgent("Battery", "agents.GridAgent", null);
			AgentController photo = cc.createNewAgent("Photo", "agents.GridAgent", null);
			AgentController hydro = cc.createNewAgent("Hydro", "agents.GridAgent", null);
			
			tso.start();
			grid.start();
			control.start();
			batteryAggregator.start();
			derAggregator.start();
			loadAggregator.start();
			battery.start();
			photo.start();
			hydro.start();*/
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
