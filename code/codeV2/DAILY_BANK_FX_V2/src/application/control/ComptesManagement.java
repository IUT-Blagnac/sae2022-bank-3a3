package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ComptesManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.orm.AccessCompteCourant;
import model.orm.AccessOperation;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.Table;

public class ComptesManagement {

	private Stage primaryStage;
	private ComptesManagementController cmc;
	private DailyBankState dbs;
	private Client clientDesComptes;

	/**
	 * Permet l'affichage de la fenêtre de la gestion des comptes d'un client
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @param client Le client
	 * @see DailyBankState
	 * @see Client
	 */
	public ComptesManagement(Stage _parentStage, DailyBankState _dbstate, Client client) {

		this.clientDesComptes = client;
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ComptesManagementController.class.getResource("comptesmanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des comptes");
			this.primaryStage.setResizable(false);
			this.cmc = loader.getController();
			this.cmc.initContext(this.primaryStage, this, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de la gestion des comptes
	 * @see ComptesManagementController
	 * 
	 */
	public void doComptesManagementDialog() {
		this.cmc.displayDialog();
	}

	/**
	 * Permet à l'utilisateur d'interagir avec la gestion des opérations du controleur de la gestion des comptes d'un client
	 * @param cpt Le compte courant
	 * @see OperationsManagement
	 * @see CompteCourant
	 */
	public void gererOperations(CompteCourant cpt) {
		OperationsManagement om = new OperationsManagement(this.primaryStage, this.dbs, this.clientDesComptes, cpt);
		om.doOperationsManagementDialog();
	}


	/**
	 * Creer un compte
	 * @return Le compte crée
	 */
	public CompteCourant creerCompte() {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);

		compte = cep.doCompteEditorDialog(this.clientDesComptes, null, EditionMode.CREATION);

		AccessCompteCourant ac = new AccessCompteCourant();

		if (compte != null) {
			try {

				ac.insertCompteCourant(compte);

				if (Math.random() < -1) {
					throw new ApplicationException(Table.CompteCourant, Order.INSERT, "todo : test exceptions", null);
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
		return compte;
	}

	/**
	 * Affiche les comptes courant d'un client
	 * @return la liste des comptes courant d'un client
	 */
	public ArrayList<CompteCourant> getComptesDunClient() {
		ArrayList<CompteCourant> listeCpt = new ArrayList<>();

		try {
			AccessCompteCourant acc = new AccessCompteCourant();
			listeCpt = acc.getCompteCourants(this.clientDesComptes.idNumCli);
		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeCpt = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeCpt = new ArrayList<>();
		}
		return listeCpt;
	}

	/**
	 * Désactive un compte
	 * @param compte le compte courant
	 */
	public void desactiverCompte(CompteCourant compte) {
		AccessCompteCourant ac = new AccessCompteCourant();
		if (compte != null) {
			try {
				AccessOperation ao = new AccessOperation();
				if(compte.solde < 0) {
					ao.insertCredit(compte.idNumCompte, -compte.solde, "Paiement Carte Bleue");
				}
				else if(compte.solde > 0) {
					ao.insertDebit(compte.idNumCompte, compte.solde, "Retrait Carte Bleue", this.dbs);
				}
				ac.supprimerCompteCourant(compte);

				if (Math.random() < -1) {
					throw new ApplicationException(Table.CompteCourant, Order.UPDATE, "todo : test exceptions", null);
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
	 * Modifie le compte en paramètre
	 * @param cpt Le compte courant
	 * @return le compte
	 */
	public CompteCourant modifierCompte(CompteCourant cpt) {
		CompteCourant compte;
		CompteEditorPane cep = new CompteEditorPane(this.primaryStage, this.dbs);

		compte = cep.doCompteEditorDialog(this.clientDesComptes, cpt, EditionMode.MODIFICATION);

		AccessCompteCourant ac = new AccessCompteCourant();

		if (compte != null) {
			try {
				ac.updateCompteCourant(compte);

				if (Math.random() < -1) {
					throw new ApplicationException(Table.CompteCourant, Order.UPDATE, "todo : test exceptions", null);
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
		return compte;
		
	}
	
	/**
	 * Permet à l'utilisateur d'interagir avec la gestion des prélèvements du controleur de la gestion des comptes d'un client
	 * @param client Le client
	 * @see PrelevementsManagement
	 */
	public void gererPrelevements(Client client) {
		PrelevementsManagement pm = new PrelevementsManagement(this.primaryStage, this.dbs, client);
		pm.doPrelevementsManagementDialog();
	}
}
