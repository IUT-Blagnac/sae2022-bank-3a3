package application.control;

import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientEditorPaneController;
import application.view.ClientsManagementController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;


public class ClientEditorPane {

	private Stage primaryStage;
	private ClientEditorPaneController cepc;
	
	/**
	 * Permet l'affichage de la fenêtre de l'édition de client
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @see DailyBankState
	 */
	public ClientEditorPane(Stage _parentStage, DailyBankState _dbstate) {

		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("clienteditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un client");
			this.primaryStage.setResizable(false);

			this.cepc = loader.getController();
			this.cepc.initContext(this.primaryStage, _dbstate);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de l'édition de client
	 * @param client Le client
	 * @param em Le mode d'édition
	 * @return displayDialog permettant l'édition du client en fonction du mode d'édition
	 * @see EditionMode
	 * @see ClientEditorPaneController
	 * @see Client
	 */
	public Client doClientEditorDialog(Client client, EditionMode em) {
		return this.cepc.displayDialog(client, em);
	}
}
