package application.view;

import java.net.URL;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.ExceptionDialog;
import application.tools.AlertUtilities;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Employe;
import model.orm.exception.ApplicationException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class EmployeEditorPaneController implements Initializable {

	// Etat application
	private DailyBankState dbs;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Employe employeEdite;
	private EditionMode em;
	private Employe employeResult;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, DailyBankState _dbstate) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));
	}

	public Employe displayDialog(Employe employe, EditionMode mode) {

		this.em = mode;
		if (employe == null) {
			this.employeEdite = new Employe(0, "", "", "", "", "",  this.dbs.getEmpAct().idAg);
		} else {
			this.employeEdite = new Employe(employe);
		}
		this.employeResult = null;
		switch (mode) {
		case CREATION:
			this.txtIdcemp.setDisable(true);

			this.txtNom.setDisable(false);
			this.txtPrenom.setDisable(false);
			this.txtIdAg.setDisable(true);
			this.txtLogin.setDisable(false);
			this.txtMdp.setDisable(false);
			this.rbChef.setSelected(true);
			this.rbGuichetier.setSelected(false);

			if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
				this.rbChef.setDisable(false);
				this.rbGuichetier.setDisable(false);
			} else {
				this.rbChef.setDisable(true);
				this.rbGuichetier.setDisable(true);
			}
			this.lblMessage.setText("Informations sur le nouveau employé");
			this.butOk.setText("Ajouter");
			this.butCancel.setText("Annuler");
			break;
		case MODIFICATION:
			if (employe.motPasse.length()==1) {

				this.txtIdcemp.setDisable(true);
				this.txtNom.setDisable(true);
				this.txtPrenom.setDisable(true);
				this.txtIdAg.setDisable(true);
				this.txtLogin.setDisable(true);
				this.txtMdp.setDisable(true);
				this.rbChef.setSelected(true);
				this.rbGuichetier.setSelected(false);
				if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
					this.rbChef.setDisable(true);
					this.rbGuichetier.setDisable(true);
				} else {
					this.rbChef.setDisable(true);
					this.rbGuichetier.setDisable(true);
				}
				this.lblMessage.setText("Informations employé");
				this.butOk.setText("Modifier");
				this.butCancel.setText("Annuler");
				this.butOk.setDisable(true);



			}else {


				this.txtIdcemp.setDisable(true);
				this.txtNom.setDisable(false);
				this.txtPrenom.setDisable(false);
				this.txtIdAg.setDisable(true);
				this.txtLogin.setDisable(false);
				this.txtMdp.setDisable(false);
				this.rbChef.setSelected(true);
				this.rbGuichetier.setSelected(false);
				if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
					this.rbChef.setDisable(false);
					this.rbGuichetier.setDisable(false);
				} else {
					this.rbChef.setDisable(true);
					this.rbGuichetier.setDisable(true);
				}
				this.lblMessage.setText("Informations employé");
				this.butOk.setText("Modifier");
				this.butCancel.setText("Annuler");
			}
			break;
		case SUPPRESSION:
			ApplicationException ae = new ApplicationException(Table.NONE, Order.OTHER, "SUPPRESSION EMPLOYE NON PREVUE",
					null);
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();

			break;
		}
		// Paramétrages spécifiques pour les chefs d'agences
		if (ConstantesIHM.isAdmin(this.dbs.getEmpAct())) {
			// rien pour l'instant
		}
		// initialisation du contenu des champs
		this.txtIdcemp.setText("" + this.employeEdite.idEmploye);
		this.txtNom.setText(this.employeEdite.nom);
		this.txtPrenom.setText(this.employeEdite.prenom);
		this.txtMdp.setText(""+this.employeEdite.motPasse);
		this.txtLogin.setText(this.employeEdite.login);
		this.txtIdAg.setText(""+this.employeEdite.idAg);

		if (ConstantesIHM.isAdmin(this.employeEdite)) {
			this.rbGuichetier.setSelected(true);
		} else {
			this.rbGuichetier.setSelected(false);
		}

		this.employeResult = null;

		this.primaryStage.showAndWait();
		return this.employeResult;
	}

	// Gestion du stage
	private Object closeWindow(WindowEvent e) {
		this.doCancel();
		e.consume();
		return null;
	}

	// Attributs de la scene + actions
	@FXML
	private Label lblMessage;
	@FXML
	private TextField txtIdcemp;
	@FXML
	private TextField txtNom;
	@FXML
	private TextField txtPrenom;
	@FXML
	private TextField txtLogin;
	@FXML
	private TextField txtMdp;
	@FXML
	private TextField txtIdAg;
	@FXML
	private RadioButton rbGuichetier;
	@FXML
	private RadioButton rbChef;
	@FXML
	private ToggleGroup actifInactif;
	@FXML
	private Button butOk;
	@FXML
	private Button butCancel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.employeResult = null;
		this.primaryStage.close();
	}

	@FXML
	private void doAjouter() {
		switch (this.em) {
		case CREATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case MODIFICATION:
			if (this.isSaisieValide()) {
				this.employeResult = this.employeEdite;
				this.primaryStage.close();
			}
			break;
		case SUPPRESSION:
			this.employeResult = this.employeEdite;
			this.primaryStage.close();
			break;
		}

	}

	private boolean isSaisieValide() {
		this.employeEdite.nom = this.txtNom.getText().trim();
		this.employeEdite.prenom = this.txtPrenom.getText().trim();
		this.employeEdite.login = this.txtLogin.getText().trim();
		this.employeEdite.motPasse = this.txtMdp.getText().trim();
		//this.employeEdite.idAg = this.txtIdAg.getText().trim();
		if (this.rbChef.isSelected()) {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_CHEF;
		} else {
			this.employeEdite.droitsAccess = ConstantesIHM.AGENCE_GUICHETIER;
		}
		if (this.employeEdite.nom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le nom ne doit pas être vide",
					AlertType.WARNING);
			this.txtNom.requestFocus();
			return false;
		}
		if (this.employeEdite.prenom.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le prénom ne doit pas être vide",
					AlertType.WARNING);
			this.txtPrenom.requestFocus();
			return false;
		}
		if (this.employeEdite.login.isEmpty()) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "Le login ne doit pas être vide",
					AlertType.WARNING);
			this.txtLogin.requestFocus();
			return false;
		}
		if (this.employeEdite.motPasse.length() <=1) {
			AlertUtilities.showAlert(this.primaryStage, "Erreur de saisie", null, "La longueur du mdp doit etre supérieur à 1",
					AlertType.WARNING);
			this.txtMdp.requestFocus();
			return false;
		}
		return true;
	}
}
