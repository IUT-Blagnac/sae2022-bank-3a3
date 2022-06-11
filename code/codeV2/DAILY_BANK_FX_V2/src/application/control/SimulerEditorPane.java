package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.SimulerEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SimulerEditorPane {

	private Stage primaryStage;
	private SimulerEditorPaneController sepc;
	
	/**
	 * Permet l'affichage de la fenêtre de la simulation d'emprunt et assurance pour les clients
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @see DailyBankState
	 */
	public SimulerEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("simulereditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Simulation et assurance d'emprunt");
			this.primaryStage.setResizable(false);

			this.sepc = loader.getController();
			this.sepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de la simulation et assurance emprunt
	 * @see EditionMode
	 * @see SimulerEditorPaneController
	 */
	public void doSimulerEditorDialog() {
		this.sepc.displayDialog();
	}
	
}
