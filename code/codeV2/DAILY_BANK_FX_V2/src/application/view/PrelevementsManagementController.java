package application.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import application.DailyBankState;
import application.control.PrelevementsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.Prelevement;

public class PrelevementsManagementController implements Initializable {

	@SuppressWarnings("unused")
	private DailyBankState dbs;
	private PrelevementsManagement pm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<Prelevement> olp;

	public void initContext(Stage _primaryStage, PrelevementsManagement _pm, DailyBankState _dbstate, Client client) {
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.pm = _pm;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olp = FXCollections.observableArrayList();
		this.lvPrelevements.setItems(this.olp);
		this.lvPrelevements.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvPrelevements.getFocusModel().focus(-1);
		this.lvPrelevements.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());
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

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private TextField txtNum;
	@FXML
	private ListView<Prelevement> lvPrelevements;
	@FXML
	private Button btnModifPre;
	@FXML
	private Button btnSuppPre;
	@FXML
	private Button btnCreerPre;

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

		ArrayList<Prelevement> listePre;
		listePre = this.pm.getlistePrelevements(this.clientDesComptes.idNumCli , numCompte);

		this.olp.clear();
		for (Prelevement prel : listePre) {
			this.olp.add(prel);
		}

		this.validateComponentState();
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}

	@FXML
	private void doSupprimerPrelevement() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement pre = this.olp.get(selectedIndice);
			Alert dialog = new Alert(AlertType.CONFIRMATION);
			dialog.setTitle("Confirmation");
			dialog.setContentText("Voulez-vous vraiment supprimer le prélèvement ?");
			dialog.setHeaderText("Supprimer le prélèvement ?");
			dialog.initOwner(this.primaryStage);
			Optional<ButtonType> reponse = dialog.showAndWait();
			if (reponse.get() == ButtonType.OK) {
				this.pm.supprimerPrelevement(pre);
				doRechercher();
			}
		}
	}

	@FXML
	private void doNouveauPrelevement() {
		Prelevement prelevement;
		prelevement = this.pm.nouveauPrelevement(this.clientDesComptes);
		if (prelevement != null) {
			this.olp.add(prelevement);
			doRechercher();
		}
	}
	
	@FXML
	private void doModifierPrelevement() {
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			Prelevement prelevement = this.pm.modifierPrelevement(this.clientDesComptes, this.olp.get(selectedIndice));
			if(prelevement != null) {
				this.olp.set(selectedIndice, prelevement);
				doRechercher();
			}
		}
	}


	private void validateComponentState() {
		this.btnSuppPre.setDisable(true);
		int selectedIndice = this.lvPrelevements.getSelectionModel().getSelectedIndex();
		if (selectedIndice >= 0) {
			this.btnModifPre.setDisable(false);
			this.btnSuppPre.setDisable(false);
		} else {
			this.btnModifPre.setDisable(true);
			this.btnSuppPre.setDisable(true);
		}
	}
}