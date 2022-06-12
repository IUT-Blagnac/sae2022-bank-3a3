package application.control;

import java.util.ArrayList;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.EmployeManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Employe;
import model.orm.AccessEmploye;
import model.orm.exception.ApplicationException;
import model.orm.exception.DatabaseConnexionException;

public class EmployeManagement {

	private Stage primaryStage;
	private DailyBankState dbs;
	private EmployeManagementController emc;



	/**
	 * Permet l'affichage de la fênetre de la gestion des employés
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @see DailyBankState
	 */
	public EmployeManagement(Stage _parentStage, DailyBankState _dbstate) {
		this.dbs = _dbstate;
		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("employemanagement.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+50, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion des employés");
			this.primaryStage.setResizable(false);

			this.emc = loader.getController();

			this.emc.initContext(this.primaryStage, this, _dbstate);





		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de la gestion des employés
	 * @see EmployeManagementController
	 */
	public void doEmployeeManagementDialog() {
		this.emc.displayDialog();
	}

	/**
	 * Modifie l'employé en paramètre
	 * @param emp Employé
	 * @return l'employé modifié
	 */
	public Employe modifierEmploye(Employe emp) {
		EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dbs);
		Employe result = cep.doEmployeEditorDialog(emp, EditionMode.MODIFICATION);
		if (result != null) {
			try {
				AccessEmploye ac = new AccessEmploye();
				ac.updateEmploye(result);

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
	 * Met à jour l'employé en paramètre
	 * @param emp Employé
	 */
	public void updateEmploye(Employe emp) {
		if (emp != null) {
			try {
				AccessEmploye ac = new AccessEmploye();
				ac.updateEmploye(emp);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				emp = null;
				this.primaryStage.close();
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				emp = null;
			}
		}
	}

	/**
	 * Créer un nouveau employé
	 * @return L'employé
	 */
	public Employe nouveauEmploye() {
		Employe employe;
		EmployeEditorPane cep = new EmployeEditorPane(this.primaryStage, this.dbs);
		employe = cep.doEmployeEditorDialog(null, EditionMode.CREATION);

		if (employe != null) {
			try {
				AccessEmploye ac = new AccessEmploye();

				ac.insertEmploye(employe);

			} catch (DatabaseConnexionException e) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
				ed.doExceptionDialog();
				this.primaryStage.close();
				employe = null;
			} catch (ApplicationException ae) {
				ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
				ed.doExceptionDialog();
				employe = null;
			}
		}
		return employe;
	}

	/**
	 * Obtenir la liste des employés en fonction du numéro, nom et prénom
	 * @param _numEmploye Numéro de l'employé
	 * @param _debutNom Nom de l'employé
	 * @param _debutPrenom Prénom de l'employé 
	 * @return
	 */
	public ArrayList<Employe> getlisteEmp(int _numEmploye, String _debutNom, String _debutPrenom) {
		ArrayList<Employe> listeEmp = new ArrayList<>();
		try {

			AccessEmploye ac = new AccessEmploye();
			listeEmp = ac.getEmploye(this.dbs.getEmpAct().idAg, _numEmploye, _debutNom, _debutPrenom);

		} catch (DatabaseConnexionException e) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, e);
			ed.doExceptionDialog();
			this.primaryStage.close();
			listeEmp = new ArrayList<>();
		} catch (ApplicationException ae) {
			ExceptionDialog ed = new ExceptionDialog(this.primaryStage, this.dbs, ae);
			ed.doExceptionDialog();
			listeEmp = new ArrayList<>();
		}
		return listeEmp;
	}

}
