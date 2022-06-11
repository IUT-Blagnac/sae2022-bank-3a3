package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.ConstantesIHM;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.orm.AccessClient;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class ClientsManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private ClientsManagementController cmc;

	/**
	 * Permet l'affichage de la fenêtre de la gestion des clients
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @see DailyBankState
	 */
	public ClientsManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clientsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des clients");
			this.primaryStage.setResizable(false);

			this.cmc = loader.getController();
			this.cmc.initContext(this.primaryStage, this, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de la gestion des clients
	 * @see ClientsManagementController
	 */
	public void doClientManagementDialog() {
		this.cmc.displayDialog();
	}

	/**
	 * Modifie un client
	 * @param c Le client
	 * @return Le client avec les modifications
	 * @see Client
	 */
	public Client modifierClient(Client c) {
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dbs);
		Client result = cep.doClientEditorDialog(c, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessClient ac = new AccessClient();
				ac.updateClient(result);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				result = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				result = null;
			}
		}
		return result;
	}
	
	
	/**
	 * Simule un emprunt
	 * @param c Le client
	 * @return Le client 
	 * @see Client
	 */

	public void SimulerEditor() {
		SimulerEditorPane cep = new SimulerEditorPane(this.primaryStage, this.dbs);
		cep.doSimulerEditorDialog();
	}
	
	
	/**
	 * Rend inactif un client
	 * @param c Le Client
	 * @return 
	 * @see Client
	 */
	public Client rendreInactif(Client c) {
		c.estInactif = ConstantesIHM.CLIENT_INACTIF;
		if (c != null) {
			try {
				AccessClient ac = new AccessClient();
				ac.updateClient(c);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				c = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				c = null;
			}
		}
		return c;
	}
		

	/**
	 * Créer un nouveau client
	 * @return le nouveau client
	 */
	public Client nouveauClient() {
		Client client;
		ClientEditorPane cep = new ClientEditorPane(this.primaryStage, this.dbs);
		client = cep.doClientEditorDialog(null, EditionMode.CREATION);
		if (client != null) {
			try {
				AccessClient ac = new AccessClient();

				ac.insertClient(client);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				client = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				client = null;
			}
		}
		return client;
	}

	/** Permet de gerer les comptes d'un client
	 * @param c Le client
	 * @see Client
	 */
	public void gererComptesClient(Client c) {
		ComptesManagement cm = new ComptesManagement(this.primaryStage, this.dbs, c);
		cm.doComptesManagementDialog();
	}

	/**
	 * Affiche la liste des comptes d'un client
	 * @param _numCompte Le numéro du compte
	 * @param _debutNom Le début du nom du client
	 * @param _debutPrenom Le début du prénom du client
	 * @return la liste des comptes du client
	 */
	public ArrayList<Client> getlisteComptes(int _numCompte, String _debutNom, String _debutPrenom) {
		ArrayList<Client> listeCli = new ArrayList<>();
		try {
			// Recherche des clients en BD. cf. AccessClient > getClients(.)
			// numCompte != -1 => recherche sur numCompte
			// numCompte != -1 et debutNom non vide => recherche nom/prenom
			// numCompte != -1 et debutNom vide => recherche tous les clients

			AccessClient ac = new AccessClient();
			listeCli = ac.getClients(this.dbs.getEmpAct().idAg, _numCompte, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCli = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeCli = new ArrayList<>();
		}
		return listeCli;
	}
}
