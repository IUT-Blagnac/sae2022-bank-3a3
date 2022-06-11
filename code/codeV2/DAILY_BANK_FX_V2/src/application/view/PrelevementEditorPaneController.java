package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.tools.AlertUtilities;
import application.tools.EditionMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Prelevement;

public class PrelevementEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Prelevement preEdite;
	private EditionMode em;
	private Prelevement preResult;
	private Client client;
	private ObservableList<CompteCourant> olCompteCourant;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	public void initContext(Stage _primaryStage, DailyBankState _dbstate, Client client) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.client = client;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdPre;
	@FXML
	private TextField txtBenef;
	@FXML
	private TextField txtMontant;
	@FXML
	private TextField txtJDP;
	@FXML
	private ComboBox<CompteCourant> cbComptes;
	@FXML
	private Button btnOk;

	@FXML
	private void doCancel() {
		this.preResult = null;
		this.primaryStage.close();
	}

	public Prelevement displayDialog(Prelevement prelevement, EditionMode mode) {
		this.em = mode;
		if(prelevement == null) {
			this.preEdite = new Prelevement();
		}else {
			this.preEdite = new Prelevement(prelevement);
		}
		this.preResult = null;
		switch(mode) {
		case CREATION:
			this.txtIdPre.setDisable(true);
			this.txtBenef.setDisable(false);
			this.txtMontant.setDisable(false);
			this.txtJDP.setDisable(false);
			this.cbComptes.setDisable(false);
			this.lblMessage.setText("Informations sur le nouveau prélèvement");
			this.btnOk.setText("Ajouter");
			break;
		case MODIFICATION:
			this.txtIdPre.setDisable(true);
			this.txtBenef.setDisable(true);
			this.txtMontant.setDisable(false);
			this.txtJDP.setDisable(false);
			this.cbComptes.setDisable(false);
			this.lblMessage.setText("Informations prélèvement");
			this.btnOk.setText("Modifier");
			break;
		case SUPPRESSION:
			break;
		}

		this.txtIdPre.setText("" + this.preEdite.idPrelev);
		this.txtBenef.setText("" + this.preEdite.beneficiaire);
		this.txtMontant.setText("" + this.preEdite.montant);
		this.txtJDP.setText("" + this.preEdite.dateRe);

		ComptesManagement comptes = new ComptesManagement(this.primaryStage, this.dbs, this.client);
		ArrayList<CompteCourant> cpts = comptes.getComptesDunClient();
		this.olCompteCourant = FXCollections.observableArrayList();
		olCompteCourant.clear();
		for (CompteCourant cc : cpts) {
			if(cc.estCloture.equals("N")) {
				olCompteCourant.add(cc);
			}
		}

		this.cbComptes.setItems(olCompteCourant);

		this.preResult = null;

		this.primaryStage.showAndWait();

		return this.preResult;

	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.preResult = this.preEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.preResult = this.preEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			break;
		}

	}

	private boolean isSaisieValide() {
		this.preEdite.beneficiaire = this.txtBenef.getText().trim();
		if(isDouble(this.txtMontant)) {
			this.preEdite.montant = Double.parseDouble(this.txtMontant.getText().trim());
		}
		if(isNumber(this.txtJDP)) {
			this.preEdite.dateRe = Integer.parseInt(this.txtJDP.getText().trim());
		}
		if(this.cbComptes.getValue() != null) {
			this.preEdite.idNumCompte = this.cbComptes.getValue().idNumCompte;
		}
		if(this.preEdite.beneficiaire.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le bénéficiaire ne doit pas être vide",
					AlertType.WARNING);
			this.txtBenef.requestFocus();
			return false;
		}
		if(!(isDouble(this.txtMontant) && Double.parseDouble(this.txtMontant.getText().trim()) > 0)) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le montant doit nombre positif et non vide",
					AlertType.WARNING);
			this.txtMontant.requestFocus();
			this.txtMontant.setText("" + this.preEdite.montant);
			return false;
		}
		if(!(isNumber(this.txtJDP) && Integer.parseInt(this.txtJDP.getText().trim()) > 0 && Integer.parseInt(this.txtJDP.getText().trim()) <= 28)) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le jour doit être entre 1 et 28",
					AlertType.WARNING);
			this.txtJDP.requestFocus();
			this.txtJDP.setText("" + this.preEdite.dateRe);
			return false;
		}
		if(this.cbComptes.getValue() == null) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Un compte doît être sélectionné",
					AlertType.WARNING);
			this.cbComptes.requestFocus();
			return false;
		}
		return true;
	}

	private boolean isNumber(TextField message) {
		try {
			Integer.parseInt(message.getText());
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	private boolean isDouble(TextField message) {
		try {
			Double.parseDouble(message.getText());
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

}