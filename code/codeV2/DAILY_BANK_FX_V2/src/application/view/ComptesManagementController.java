package application.view;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import application.DailyBankState;
import application.control.ComptesManagement;
import application.control.OperationsManagement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class ComptesManagementController implements Initializable {

	// Etat application
	private DailyBankState dbs;
	private ComptesManagement cm;

	// Fenêtre physique
	private Stage primaryStage;

	// Données de la fenêtre
	private Client clientDesComptes;
	private ObservableList<CompteCourant> olCompteCourant;

	// Manipulation de la fenêtre
	public void initContext(Stage _primaryStage, ComptesManagement _cm, DailyBankState _dbstate, Client client) {
		this.cm = _cm;
		this.primaryStage = _primaryStage;
		this.dbs = _dbstate;
		this.clientDesComptes = client;
		this.configure();
	}

	private void configure() {
		String info;

		this.primaryStage.setOnCloseRequest(e -> this.closeWindow(e));

		this.olCompteCourant = FXCollections.observableArrayList();
		this.lvComptes.setItems(this.olCompteCourant);
		this.lvComptes.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		this.lvComptes.getFocusModel().focus(-1);
		this.lvComptes.getSelectionModel().selectedItemProperty().addListener(e -> this.validateComponentState());

		info = this.clientDesComptes.nom + "  " + this.clientDesComptes.prenom + "  (id : "
				+ this.clientDesComptes.idNumCli + ")";
		this.lblInfosClient.setText(info);

		this.loadList();
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
	private Label lblInfosClient;
	@FXML
	private ListView<CompteCourant> lvComptes;
	@FXML
	private Button btnVoirOpes;
	@FXML
	private Button btnModifierCompte;
	@FXML
	private Button btnDesactiverCompte;
	@FXML
	private Button btnPDF;
	@FXML
	private Button btnVoirPrelevements;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	@FXML
	private void doCancel() {
		this.primaryStage.close();
	}
	
	@FXML
	private void doVoirPrelevements() {
		this.cm.gererPrelevements(this.clientDesComptes);
	}

	@SuppressWarnings("deprecation")
	@FXML
	private void doPDF() {

		Document doc = new Document();
		
		try {
			
			PdfWriter.getInstance(doc, new FileOutputStream("releve_mensuel_" + this.clientDesComptes.nom + "_" + this.clientDesComptes.prenom + "_" + LocalDate.now().getMonthValue() +"_" + LocalDate.now().getYear() +".pdf"));
			doc.open();

			doc.add(new Paragraph("Relevé mensuel du client " + this.clientDesComptes.nom + " " + this.clientDesComptes.prenom + " du " + LocalDate.now().getMonthValue() +"/" + LocalDate.now().getYear()));
			doc.add(new Paragraph(" "));
			doc.add(new Paragraph("-------------------------------------------------------"));
			doc.add(new Paragraph(" "));
			
			for(int i = 0 ; i < this.olCompteCourant.size() ; i++) {
				doc.add(new Paragraph("- "+this.olCompteCourant.get(i).toString()));
				doc.add(new Paragraph(" "));
				
				OperationsManagement ops = new OperationsManagement(primaryStage, dbs, clientDesComptes, this.olCompteCourant.get(i));
				
				ArrayList<Operation> listOp = ops.operationsEtSoldeDunCompte().getRight();

				for(int j = 0 ; j < listOp.size() ; j ++) {
					if(listOp.get(j).dateOp.getMonth() == LocalDate.now().getMonthValue());{
						doc.add(new Paragraph(listOp.get(j).toString()));	
					}
				}
				doc.add(new Paragraph(" "));
				doc.add(new Paragraph("-------------------------------------------------------"));
				doc.add(new Paragraph(" "));
			}
			
			
			doc.close();
			Desktop.getDesktop().open(new File("releve_mensuel_" + this.clientDesComptes.nom + "_" + this.clientDesComptes.prenom + "_" + LocalDate.now().getMonthValue() +"_" + LocalDate.now().getYear() +".pdf"));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		}

		@FXML
		private void doVoirOperations() {
			int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
				this.cm.gererOperations(cpt);
			}
			this.loadList();
			this.validateComponentState();
		}

		@FXML
		private void doModifierCompte() {
			int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
				this.cm.modifierCompte(cpt);
				loadList();
			}
		}

		@FXML
		private void doDesactiverCompte() {
			int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				CompteCourant cpt = this.olCompteCourant.get(selectedIndice);
				Alert dialog = new Alert(AlertType.CONFIRMATION);
				dialog.setTitle("Confirmation");
				dialog.setContentText("Voulez-vous vraiment clôturer le compte ?");
				dialog.setHeaderText("Clôturer le compte ?");
				dialog.initOwner(this.primaryStage);
				Optional<ButtonType> reponse = dialog.showAndWait();
				if (reponse.get() == ButtonType.OK) {
					this.cm.desactiverCompte(cpt);
					loadList();
				}
			}
		}

		@FXML
		private void doNouveauCompte() {
			CompteCourant compte;
			compte = this.cm.creerCompte();
			if (compte != null) {
				this.olCompteCourant.add(compte);
				loadList();
			}
		}

		private void loadList () {
			ArrayList<CompteCourant> listeCpt;
			listeCpt = this.cm.getComptesDunClient();
			this.olCompteCourant.clear();
			for (CompteCourant co : listeCpt) {
				this.olCompteCourant.add(co);
			}
		}

		private void validateComponentState() {
			// Non implémenté => désactivé
			this.btnModifierCompte.setDisable(true);
			this.btnDesactiverCompte.setDisable(true);
			this.btnPDF.setDisable(true);
			
			if (!this.olCompteCourant.isEmpty()) {
				this.btnPDF.setDisable(false);
			}
			this.btnVoirPrelevements.setDisable(true);
			CompteCourant compte;
			if(!this.olCompteCourant.isEmpty()) {
				this.btnVoirPrelevements.setDisable(false);
			}
			int selectedIndice = this.lvComptes.getSelectionModel().getSelectedIndex();
			if (selectedIndice >= 0) {
				this.btnModifierCompte.setDisable(false);
				compte = this.olCompteCourant.get(selectedIndice);
				if(compte.estCloture.equals("O")) {
					this.btnVoirOpes.setDisable(true);
					this.btnDesactiverCompte.setDisable(true);
				}
				else {
					this.btnVoirOpes.setDisable(false);
					this.btnDesactiverCompte.setDisable(false);
				}
			} else {
				this.btnVoirOpes.setDisable(true);
				this.btnDesactiverCompte.setDisable(true);
			}
		}
	}
