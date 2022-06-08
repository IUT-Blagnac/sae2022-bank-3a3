package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.StageManagement;
import application.view.OperationEditorPaneController;
import application.view.VirementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.CompteCourant;
import model.data.Operation;

public class VirementEditorPane {

	private Stage primaryStage;
	private VirementEditorPaneController vepc;
	
	/**
	 * Permet l'affichage de la fenêtre de l'édition de virement
	 * @param _parentStage La fenêtre parente
	 * @param _cm Les comptes du client
	 * @param _dbstate La banque
	 * @param c Client
	 * @param cc Compte courant actuel
	 * @see ComptesManagement
	 * @see DailyBankState
	 * @see Client
	 * @see CompteCourant
	 */
	public VirementEditorPane(Stage _parentStage, ComptesManagement _cm, DailyBankState _dbstate, Client c, CompteCourant cc) {

		try {
			FXMLLoader loader = new FXMLLoader(
					OperationEditorPaneController.class.getResource("virementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Enregistrement d'un virement");
			this.primaryStage.setResizable(false);

			this.vepc = loader.getController();
			this.vepc.initContext(this.primaryStage, _cm, _dbstate, c, cc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de l'édition des virements
	 * @return displayDialog permettant l'édition d'un virement d'un compte courant d'un client
	 * @see VirementEditorPaneController
	 */
	public Operation[] doVirementEditorDialog() {
		return this.vepc.displayDialog();
	}
}

