package model.orm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.data.Prelevement;
import model.orm.exception.DataAccessException;
import model.orm.exception.DatabaseConnexionException;
import model.orm.exception.Order;
import model.orm.exception.RowNotFoundOrTooManyRowsException;
import model.orm.exception.Table;

public class AccessPrelevement {
	public AccessPrelevement() {
	}

	/**
	 * Recherche de tous prélèvements d'un client.
	 *
	 * @param idNumCli id du client dont on cherche tous les prélèvements
	 * @return Tous les prélèvement du client, liste vide si pas de prélèvements
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public ArrayList<Prelevement> getPrelevements(int idNumCli, int idNumCompte) throws DataAccessException, DatabaseConnexionException {
		ArrayList<Prelevement> alResult = new ArrayList<>();

		try {
			Connection con = LogToDatabase.getConnexion();

			PreparedStatement pst;

			String query;
			if(idNumCompte != -1) {
				query = "SELECT * FROM PrelevementAutomatique PA, CompteCourant CC, Client C WHERE PA.idNumCompte = CC.idNumCompte AND C.idNumCli = CC.idNumCli AND C.idNumCli= ?";
				query += " AND CC.idNumCompte = ?";
				query += " ORDER BY idPrelev";
				pst = con.prepareStatement(query);
				pst.setInt(1, idNumCli);
				pst.setInt(2, idNumCompte);
			} else {
				query = "SELECT * FROM PrelevementAutomatique PA, CompteCourant CC, Client C WHERE PA.idNumCompte = CC.idNumCompte AND C.idNumCli = CC.idNumCli AND C.idNumCli= ?";
				query += " ORDER BY idPrelev";
				pst = con.prepareStatement(query);
				pst.setInt(1, idNumCli);
			}
			System.err.println(query);

			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				int idPrelev = rs.getInt("idPrelev");
				double montant = rs.getDouble("montant");
				int dateRe = rs.getInt("dateRecurrente");
				String benef = rs.getString("beneficiaire");
				int idNumCompteTrouve = rs.getInt("idNumCompte");

				alResult.add(new Prelevement(idPrelev, montant, dateRe, benef, idNumCompteTrouve));
			}
			rs.close();
			pst.close();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.SELECT, "Erreur accès", e);
		}
		return alResult;
	}

	/**
	 * Supprime un prélèvement dans la table 
	 * @param pre Le prélèvement à supprimer
	 */
	public void supprimerPrelevement(Prelevement pre) throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "DELETE FROM PrelevementAutomatique WHERE idPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setInt(1, pre.idPrelev);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.DELETE,
						"Delete anormal", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.DELETE, "Erreur accès", e);
		}
	}

	/**
	 * Insert le nouveau prélèvement
	 * @param prelevement Le nouveau prélèvement
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void insertPrelevement(Prelevement prelevement) throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "INSERT INTO PRELEVEMENTAUTOMATIQUE VALUES ( seq_id_prelevauto.NEXTVAL" +", " + "?" + ", " + "?" + ", "
					+ "?" + ", " + "?)";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelevement.montant);
			pst.setInt(2, prelevement.dateRe);
			pst.setString(3, prelevement.beneficiaire);
			pst.setInt(4, prelevement.idNumCompte);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();

			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.INSERT,
						"Insert anormal (insert de moins ou plus d'une ligne)", null, result);
			}

			query = "SELECT seq_id_prelevauto.CURRVAL from DUAL";

			System.err.println(query);
			PreparedStatement pst2 = con.prepareStatement(query);

			ResultSet rs = pst2.executeQuery();
			rs.next();
			int numCompte = rs.getInt(1);

			con.commit();
			rs.close();
			pst2.close();

			prelevement.idNumCompte = numCompte;

		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.INSERT, "Erreur accès", e);
		}

	}

	/**
	 * Met à jour les informations du prélèvement
	 * @param prelevement Le prélèvement à modifier
	 * @throws RowNotFoundOrTooManyRowsException
	 * @throws DataAccessException
	 * @throws DatabaseConnexionException
	 */
	public void updatePrelevement(Prelevement prelevement)
			throws RowNotFoundOrTooManyRowsException, DataAccessException, DatabaseConnexionException {
		try {
			Connection con = LogToDatabase.getConnexion();

			String query = "UPDATE PRELEVEMENTAUTOMATIQUE SET " + "montant = " + "? , " + "dateRecurrente = " + "? , " + "idNumCompte = "
					+ "? " + "WHERE idPrelev = ?";

			PreparedStatement pst = con.prepareStatement(query);
			pst.setDouble(1, prelevement.montant);
			pst.setInt(2, prelevement.dateRe);
			pst.setInt(3, prelevement.idNumCompte);
			pst.setInt(4, prelevement.idPrelev);

			System.err.println(query);

			int result = pst.executeUpdate();
			pst.close();
			if (result != 1) {
				con.rollback();
				throw new RowNotFoundOrTooManyRowsException(Table.PrelevementAutomatique, Order.UPDATE,
						"Update anormal (update de moins ou plus d'une ligne)", null, result);
			}
			con.commit();
		} catch (SQLException e) {
			throw new DataAccessException(Table.PrelevementAutomatique, Order.UPDATE, "Erreur accès", e);
		}
	}
}
