package application;

import application.tools.ConstantesIHM;
import model.data.AgenceBancaire;
import model.data.Employe;

public class DailyBankState {
	private Employe empAct;
	private AgenceBancaire agAct;
	private boolean isChefDAgence;

	/**
	 * Affiche l'employé
	 * @return Les informations de l'employé
	 * 
	 */
	public Employe getEmpAct() {
		return this.empAct;
	}
	/**
	 * Change l'employé
	 * @param employeActif Le nouveau employé 
	 */
	public void setEmpAct(Employe employeActif) {
		this.empAct = employeActif;
	}


	/**
	 * Affiche l'agence bancaire
	 * @return L'agence bancaire
	 */
	public AgenceBancaire getAgAct() {
		return this.agAct;
	}

	/**
	 * Change l'agence bancaire
	 * @param agenceActive La nouvelle agence bancaire
	 */
	public void setAgAct(AgenceBancaire agenceActive) {
		this.agAct = agenceActive;
	}

	/**
	 * Affiche l'état de la personne si elle est chef d'agence
	 * @return vrai si la personne est chef d'agence sinon faux
	 */
	public boolean isChefDAgence() {
		return this.isChefDAgence;
	}

	/**
	 * Change l'etat du chef d'agence
	 * @param isChefDAgence l'état du chef d'agence
	 */
	public void setChefDAgence(boolean isChefDAgence) {
		this.isChefDAgence = isChefDAgence;
	}

	/** 
	 * Met la personne en tant que chef d'agence
	 * @param droitsAccess Les droits à mettre sur la personne
	 */
	public void setChefDAgence(String droitsAccess) {
		this.isChefDAgence = false;

		if (droitsAccess.equals(ConstantesIHM.AGENCE_CHEF)) {
			this.isChefDAgence = true;
		}
	}
}
