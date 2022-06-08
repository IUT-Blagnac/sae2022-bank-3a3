package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.tools.ConstantesIHM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class VirementEditorPaneController implements Initializable {
	// Etat application
	@SuppressWarnings("unused")
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;
	
	// Données de la fenêtre
	private CompteCourant compteConcerne;
	private ComptesManagement cm;
	private ObservableList<CompteCourant> olCompteCourants;
	private Operation[] operationResultat = new Operation[2];
	private CompteCourant compteChoisi;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ComptesManagement _cm, DailyBankState _dbstate, Client client, CompteCourant compte) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.cm = _cm;
		this.compteConcerne = compte;
		this.configure();
	}

	private void configure() {

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
		this.olCompteCourants = FXCollections.observableArrayList();
		this.autreComptes.setItems(this.olCompteCourants);
		this.autreComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.autreComptes.getFocusModel().focus(-1);
		loadList();
	}

	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	public Operation[] displayDialog() {
		String info = "Cpt. : " + this.compteConcerne.idNumCompte + "  "
				+ String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  "
				+ String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise);
		this.lblMessage.setText(info);

		ObservableList<String> list = FXCollections.observableArrayList();

		for (String tyOp : ConstantesIHM.OPERATIONS_DEBIT_GUICHET) {
			list.add(tyOp);
		}

		this.primaryStage.showAndWait();
		return this.operationResultat;
	}

	private void loadList () {
		ArrayList<CompteCourant> listeCpt;
		listeCpt = this.cm.getComptesDunClient();
		this.olCompteCourants.clear();
		for (int i = 0; i < listeCpt.size(); i++) {
			if(listeCpt.get(i).idNumCompte != this.compteConcerne.idNumCompte && listeCpt.get(i).estCloture.equals("N")) {
				this.olCompteCourants.add(listeCpt.get(i));
			}
		}

	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private Label lblMessage;	

	@FXML 
	private TextField textArgent;

	@FXML
	private ListView<CompteCourant> autreComptes;

	@FXML
	private void doCancel() {
		this.operationResultat = null;
		this.primaryStage.close();
	}

	@FXML
	private void doVirement() {
		double montant;
		int selectedIndice = this.autreComptes.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.compteChoisi = this.olCompteCourants.get(selectedIndice);
			try {
				montant = Double.parseDouble(this.textArgent.getText().trim());
				if (montant <= 0)
					throw new NumberFormatException();
			} catch (NumberFormatException nfe) {
				this.textArgent.requestFocus();
				return;
			}
			if (this.compteConcerne.solde - montant < this.compteConcerne.debitAutorise) {
				Alert dialog = new Alert(AlertType.INFORMATION);
				dialog.setTitle("Erreur");
				dialog.setContentText("- Cpt. : " + this.compteConcerne.idNumCompte + "  " + String.format(Locale.ENGLISH, "%12.02f", this.compteConcerne.solde) + "  /  " + String.format(Locale.ENGLISH, "%8d", this.compteConcerne.debitAutorise));
				dialog.setHeaderText("Dépassement du découvert !");
				dialog.initOwner(this.primaryStage);
				dialog.showAndWait();
				return;
			}
			this.operationResultat[0] = new Operation(-1, montant, null, null, this.compteConcerne.idNumCompte, "Virement Compte à Compte");
			this.operationResultat[1] = new Operation(-1, montant, null, null, this.compteChoisi.idNumCompte, "Virement Compte à Compte");
			this.primaryStage.close();
		}

	}
}
