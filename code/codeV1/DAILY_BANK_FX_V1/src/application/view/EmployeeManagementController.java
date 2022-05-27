
	package application.view;

	import java.net.URL;
	import java.util.ArrayList;
	import java.util.ResourceBundle;

	import application.DailyBankState;
	import application.control.ClientsManagement;
import application.control.EmployeeManagement;
import javafx.collections.FXCollections;
	import javafx.collections.ObservableList;
	import javafx.fxml.FXML;
	import javafx.fxml.Initializable;
	import javafx.scene.control.Button;
	import javafx.scene.control.ListView;
	import javafx.scene.control.SelectionMode;
	import javafx.scene.control.TextField;
	import javafx.stage.Stage;
	import javafx.stage.WindowEvent;
	import model.data.Client;
import model.data.Employe;

	public class EmployeeManagementController implements Initializable {

		// Etat application
		private DailyBankState dbs;
		private EmployeeManagement cm;

		// Fenêtre physique
		private Stage primaryStage;

		// Données de la fenêtre
		private ObservableList<Employe> olc;

		// Manipulation de la fenêtre
		public void initContext (Stage _primaryStage, EmployeeManagement _cm, DailyBankState _dbstate) {
			
			
			

			
			
			this.cm = _cm;
			this.primaryStage = _primaryStage;
			this.dbs = _dbstate;
			this.configure();
		}
		
		public void initCo() {
			
		}
		

		private void configure() {
			this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

			this.olc = FXCollections.observableArrayList();
			
			this.lvEmployes.setItems(this.olc);
			this.lvEmployes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			this.lvEmployes.getFocusModel().focus(-1);
			this.lvEmployes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
			this.validateComponentState();
		}

		public void displayDialog() {
			this.primaryStage.showAndWait();
		}
		

		// Gestion du stage
		private Object closeWindow(WindowEvent e) {
			this.doCancel();
			e.consume();
			return null;
		}

		// Attributs de la scene + actions
		@FXML
		private TextField txtNum;
		@FXML
		private TextField txtNom;
		@FXML
		private TextField txtPrenom;
		@FXML
		private ListView<Employe> lvEmployes;
		
		@FXML
		private Button btnDesactClient;
		@FXML
		private Button btnModifClient;
		@FXML
		private Button btnActiverEmp;

		@Override
		public void initialize(URL location, ResourceBundle resources) {
		}

		@FXML
		private void doCancel() {
			this.primaryStage.close();
		}

		@FXML
		private void doRechercher() {
			int numCompte;
			try {
				String nc = this.txtNum.getText();
				if (nc.equals("")) {
					numCompte = -1;
				} else {
					numCompte = Integer.parseInt(nc);
					if (numCompte < 0) {
						this.txtNum.setText("");
						numCompte = -1;
					}
				}
			} catch (NumberFormatException nfe) {
				this.txtNum.setText("");
				numCompte = -1;
			}

			String debutNom = this.txtNom.getText();
			String debutPrenom = this.txtPrenom.getText();

			if (numCompte != -1) {
				this.txtNom.setText("");
				this.txtPrenom.setText("");
			} else {
				if (debutNom.equals("") && !debutPrenom.equals("")) {
					this.txtPrenom.setText("");
				}
			}

			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte != -1 et debutNom non vide => recherche nom/prenom
			// numCompte != -1 et debutNom vide => recherche tous les clients
			
			ArrayList<Employe> listeEmp;
			listeEmp = this.cm.getlisteComptes(numCompte, debutNom, debutPrenom);

			
			
			this.olc.clear();
			for (Employe emp : listeEmp) {
				this.olc.add(emp);
				
			}
			this.validateComponentState();
			
		}

		
		
		@FXML
		private void doComptesClient() {
		//	int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
			//if (selectedIndice >= 0) {
			//	Employe employe = this.olc.get(selectedIndice);
			//	this.cm.gererComptesClient(employe);
			//}
		}

		@FXML
		private void doModifierEmploye() {

			int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				Employe cliMod = this.olc.get(selectedIndice);
				
		
		
				Employe result = this.cm.modifierEmploye(cliMod);
				if (result != null) {
					this.olc.set(selectedIndice, result);
				
			  }
			}
		}

		@FXML
		private void doDesactiverClient() {
			int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				Employe cliMod = this.olc.get(selectedIndice);
				
				cliMod.motPasse = "/";
				
			//	Employe result = this.cm.desacEmploye(cliMod);
				//if (result != null) {
				//	this.olc.set(selectedIndice, result);
				//}
			}
		}
		
		@FXML
		private void doActiverEmploye() {
			int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				Employe cliMod = this.olc.get(selectedIndice);
				
				cliMod.motPasse = "//";
				
			//	Employe result = this.cm.desacEmploye(cliMod);
				//if (result != null) {
				//	this.olc.set(selectedIndice, result);
				//}
			}
		}

		@FXML
		private void doNouveauEmploye() {
			Employe employe;
			employe = this.cm.nouveauEmploye();
		
			
			if (employe != null) {
				this.olc.add(employe);
			}
		}
		
		

		private void validateComponentState() {
			// Non implémenté => désactivé
			
			int selectedIndice = this.lvEmployes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
			
				
				
				this.btnModifClient.setDisable(false);
				this.btnActiverEmp.setDisable(false);
				this.btnDesactClient.setDisable(false);
			} else {
				this.btnModifClient.setDisable(true);
				this.btnActiverEmp.setDisable(true);
				this.btnDesactClient.setDisable(true);

			}
		}
	}


