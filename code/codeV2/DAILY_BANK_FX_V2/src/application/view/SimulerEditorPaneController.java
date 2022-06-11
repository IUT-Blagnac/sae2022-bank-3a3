package application.view;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import application.DailyBankState;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import java.lang.Math;


public class SimulerEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientEdite;
	private EditionMode em;
	private Client clientResult;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public Client displayDialog(Client client, EditionMode mode) {

		this.em = mode;
		if (client == null) {
			this.clientEdite = new Client(0, "", "", "", "", "", "N", this.dbs.getEmpAct().idAg);
		} else {
			this.clientEdite = new Client(client);
		}
		this.clientResult = null;
		switch (mode) {
		case CREATION:
		
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			this.butOk.setText("Simulation emprunt");
			this.butCancel.setText("Annuler");
			break;
		case SUPPRESSION:
			// ce mode n'est pas utilisé pour les Clients :
			// la suppression d'un client n'existe pas il faut que le chef d'agence
			// bascule son état "Actif" à "Inactif"s
			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}
		// initialisation du contenu des champs

		this.clientResult = null;

		this.primaryStage.showAndWait();
		return this.clientResult;
	}
	
	

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private TextField montant;
	
	@FXML
	private TextField annee;
	
	@FXML
	private TextField TA;
	
	@FXML
	private TextArea txt;
	
	@FXML
	private TextField montantAss;
	
	@FXML
	private TextField TauxAnnuel;
	
	@FXML
	private TextField DureeMois;
	
	
	
	
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.clientResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doSimul() {
		
		String aff = "";
		
		if (!montant.getText().isEmpty() && !annee.getText().isEmpty() && !TA.getText().isEmpty()) {
			
			int numTA= Integer.parseInt(TA.getText());
			int numA= Integer.parseInt(annee.getText());
			int numMontant= Integer.parseInt(montant.getText());
			
			aff ="Année | Capital restant dû |Intérêts | Amortissement du capital | Annuité\n";
			
			int Capital=numMontant;
			int interet=(Capital/100)*numTA;
			int amor= (Capital/numA);
			int annuite= amor+interet;
			
			
			int totI = 0;
			int totC = 0;
			int totA = 0;
			for (int i =0 ; i<numA; i++) {
				int bona =i+1;
				totI += interet;
				totC += amor;
				totA += annuite;
				aff = aff + "    "+ bona +"             |   " + Capital + "    |     " + interet + "     |               " + amor +"                  |   " + annuite + "\n";
				
				Capital = Capital-amor;
				interet = (Capital/100)*numTA;
				annuite = amor+interet;
				
				
			}
			
			
			aff = aff + "    "+ " Total " +"   |   " + "       " + "    |     " + totI + "     |               " + totC +"                  |   " + totA + "\n";
			txt.setText(aff);
			
			
			
		}
			
		}
		
		
		
		
		
		@FXML
		private void doAss() {
			
			String aff = "";
			
			if (!montantAss.getText().isEmpty() && !TauxAnnuel.getText().isEmpty() && !DureeMois.getText().isEmpty()) {
				
				float numTA= Float.parseFloat(TauxAnnuel.getText());
				float numA= Integer.parseInt(DureeMois.getText());
				double numMontant= Integer.parseInt(montantAss.getText());
				float Tapl = numTA/100/12;
				float tour = numA;
				numA = numA - numA - numA;
				System.out.println(Tapl);
				
				aff ="Num mois | Capital restant dû en début de période |Intérêts | Montant des interet | Montant du princiapl  |  Montant à rembourser (Mensualité) | Capital restant du en fin de période \n";
				
				double CapDeb=numMontant;
				double interet=CapDeb*Tapl;
				
				double MontantArembourser = numMontant * (Tapl/ (1-(Math.pow(1+Tapl, numA))));
				System.out.println(MontantArembourser);
				
				
				double princ= MontantArembourser-interet;
				
				double CapFin= CapDeb-princ;
				
			
				for (int i =0 ; i<tour; i++) {
					int bona =i+1;
					
					aff = aff + "    "+ bona +"             |                   " + CapDeb + "             |                           " + interet + "     |               " + princ +"                  |   " + MontantArembourser + "  |    " + CapFin + "\n";
					
					
					
					
					
					
					interet = CapDeb*Tapl;
					CapFin = CapDeb -princ;
					princ = MontantArembourser-interet;
					CapDeb = CapFin;
					
				
				}
				
				
				txt.setText(aff);
				
				
				
				
				
			}
		
		
	}

	

	

	

	
}
