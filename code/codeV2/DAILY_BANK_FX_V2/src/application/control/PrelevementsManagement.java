
package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.PrelevementsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Prelevement;
import model.orm.AccessPrelevement;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class PrelevementsManagement {
	private Stage primaryStage;
	private DailyBankState dbs;
	private PrelevementsManagementController pmc;
	private Client clientDuCompte;

	/**
	 * Permet l'affichage de la fênetre de la gestion des prélèvements d'un client
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @param client Le client
	 * @see DailyBankState
	 * @see Client
	 */
	public PrelevementsManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDuCompte = client;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(
					PrelevementsManagementController.class.getResource("prelevementsmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+100, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des prélèvements");
			this.primaryStage.setResizable(false);

			this.pmc = loader.getController();
			this.pmc.initContext(this.primaryStage, this, this.dbs, this.clientDuCompte);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de le la gestion des prélèvements
	 * @see PrelevementsManagementController
	 */
	public void doPrelevementsManagementDialog() {
		this.pmc.displayDialog();
	}

	/**
	 * Affiche les prélèvements d'un client
	 * @return la liste des prélèvements d'un client
	 */
	public ArrayList<Prelevement> getlistePrelevements(int numCli, int numCompte) {
		ArrayList<Prelevement> listePre = new ArrayList<>();
		try {
			AccessPrelevement ap = new AccessPrelevement();
			listePre = ap.getPrelevements(numCli, numCompte);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listePre = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listePre = new ArrayList<>();
		}
		return listePre;
	}


	/**
	 * Supprime le prélèvement
	 * @param pre Le prélèvement
	 */
	public void supprimerPrelevement(Prelevement pre) {
		if (pre != null) {
			try {
				AccessPrelevement ap = new AccessPrelevement();
				ap.supprimerPrelevement(pre);

				if (Math.random() < -1) {
					throw new ApplicationException(Table.PrelevementAutomatique, Order.DELETE, "todo : test exceptions", null);
				}
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
			}
		}
	}


	/**
	 * Ajoute un nouveau prélèvement au client
	 * @param client Le client
	 * @return Le nouveau prélèvement
	 */
	public Prelevement nouveauPrelevement(Client client) {
		Prelevement prelevement;
		PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs, client);
		prelevement = pep.doPrelevementEditorDialog(null, EditionMode.CREATION);
		if(prelevement != null){
			try {
				AccessPrelevement ap = new AccessPrelevement();

				ap.insertPrelevement(prelevement);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelevement = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				prelevement = null;
			}
		}
		return prelevement;
	}

	/**
	 * Modifie un prélèvement du client
	 * @param client Le client
	 * @return Le prélèvement modifié
	 */
	public Prelevement modifierPrelevement(Client client, Prelevement prelevementActuel) {
		Prelevement prelevement;
		PrelevementEditorPane pep = new PrelevementEditorPane(this.primaryStage, this.dbs, client);
		prelevement = pep.doPrelevementEditorDialog(prelevementActuel, EditionMode.MODIFICATION);
		if(prelevement != null){
			try {
				AccessPrelevement ap = new AccessPrelevement();

				ap.updatePrelevement(prelevement);
			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				prelevement = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				prelevement = null;
			}
		}
		return prelevement;
	}
}