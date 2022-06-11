
package application.control;


import application.DailyBankApp;
import application.DailyBankState;
import application.tools.EditionMode;
import application.tools.StageManagement;
import application.view.ClientsManagementController;
import application.view.PrelevementEditorPaneController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.data.Client;
import model.data.Prelevement;

public class PrelevementEditorPane {
	private Stage primaryStage;
	private PrelevementEditorPaneController pepc;

	/**
	 * Permet l'affichage de la fenêtre de l'édition de prélèvement
	 * @param _parentStage La fenêtre parente
	 * @param _dbstate La banque
	 * @see DailyBankState
	 */
	public PrelevementEditorPane(Stage _parentStage, DailyBankState _dbstate, Client client) {

		try {
			FXMLLoader loader = new FXMLLoader(ClientsManagementController.class.getResource("prelevementeditorpane.fxml"));
			BorderPane root = loader.load();

			Scene scene = new Scene(root, root.getPrefWidth()+20, root.getPrefHeight()+10);
			scene.getStylesheets().add(DailyBankApp.class.getResource("application.css").toExternalForm());

			this.primaryStage = new Stage();
			this.primaryStage.initModality(Modality.WINDOW_MODAL);
			this.primaryStage.initOwner(_parentStage);
			StageManagement.manageCenteringStage(_parentStage, this.primaryStage);
			this.primaryStage.setScene(scene);
			this.primaryStage.setTitle("Gestion d'un prélèvement");
			this.primaryStage.setResizable(false);

			this.pepc = loader.getController();
			this.pepc.initContext(this.primaryStage, _dbstate, client);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Permet à l'utilisateur d'interagir avec le dialogue du controleur de l'édition de prélèvement
	 * @param client Le client
	 * @param em Le mode d'édition
	 * @return displayDialog permettant l'édition d'un prélèvement en fonction du mode d'édition
	 * @see EditionMode
	 * @see PrelevementEditorPaneController
	 * @see Prelevement
	 */
	public Prelevement doPrelevementEditorDialog(Prelevement prelevement, EditionMode em) {
		return this.pepc.displayDialog(prelevement, em);
	}
}