package model.data;

public class Prelevement {


	public int idPrelev;
	public double montant;
	public int dateRe;
	public String beneficiaire;
	public int idNumCompte;


	public Prelevement(int idPrelev, double montant, int dateRe, String beneficiaire, int idNumCompte) {
		super();
		this.idPrelev = idPrelev;
		this.montant = montant;
		this.dateRe = dateRe;
		this.beneficiaire = beneficiaire;
		this.idNumCompte = idNumCompte;
	}

	public Prelevement(Prelevement p) {
		this(p.idPrelev, p.montant, p.dateRe, p.beneficiaire, p.idNumCompte);
	}

	public Prelevement() {
		this(0, 0.0, 0, "", 0);
	}

	@Override
	public String toString() {
		return this.idPrelev + " : Numéro Compte : " + this.idNumCompte + " , Montant = " + this.montant + " , Jour de paiement : " + this.dateRe + " , Bénéficiaire : " + this.beneficiaire;

	}
}