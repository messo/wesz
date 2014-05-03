package hu.magic.wesz.misc;

import java.util.Random;

import hu.magic.wesz.model.Service;
import hu.magic.wesz.model.User;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

@Startup
@Singleton
public class InitDatabase {

	@PersistenceUnit(unitName = "WeszBackend")
	EntityManagerFactory emf;

	private static final String[] titles = { "Alg�s tengeri f�rd�",
			"Bioszauna", "Finn szauna mer�l�medenc�vel", "G�zf�rd�",
			"Infraszauna", "J�gkamra", "Kering�si massz�zs", "Orosz szauna",
			"S�kamra", "Spa alg�s b�rrad�r", "T�volkeleti ter�pia",
			"Tepid�rium", "Thai gy�gyn�v�nyes massz�zs", "Thai massz�zs",
			"T�r�k pezsg�f�rd�", "Aromakabin", "Ayurv�da - Indiai fejmassz�zs",
			"Barlangzuhany" };

	private static final String[] descs = {
			"A tengeri alga fokozza az anyagcser�t �s a v�rkering�st. Az alga magas �sv�nyi anyag tartalm�nak, valamint a hidromassz�zs fizikai erej�nek k�sz�nhet�en a kor�bban elvesz�tett �sv�nyi anyagok p�tl�dnak a szervezetben. Az alga kering�sfokoz�, zs�rold� hat�s�, ez�rt a f�rd� m�regtelen�t �s karcs�s�t.",
			"Kellemes, 40-50 Celsius fokos h�m�rs�klet� szauna, amely nem megterhel� a szervezet sz�m�ra.",
			"L�nyege, hogy a szaun�t haszn�l� szem�ly teste felhev�lj�n, izzadjon, ez�ltal m�regtelen�tsen, valamint a testi-lelki pihen�s.",
			"Ha valakinek t�l magas h�fokot jelent a finn szauna, relax�lhat a g�zf�rd�ben is, melynek h�m�rs�klete alacsonyabb, kb. 43 Celsius fok, p�ratartalma viszont magasabb (100%), az izzad�s ez�rt itt is garant�lt. Mivel a test fokozatosan hev�l fel, nem terheli meg a szervezetet, �gy a kering�si probl�m�kkal k�szk�d�knek is aj�nlott.",
			"Az infraszauna nev�b�l ad�d�an m�s technik�val m�k�dik, mint a hagyom�nyos, finn szauna, helyis�g�t infrasugarakkal f�tik, amik a f�ny l�thatatlan tartom�ny�ba tartoznak. Az infrasugarak bel�lr�l meleg�tik a testet, t�g�tj�k az ereket, serkentik a v�rkering�st, illetve a szervezet felesleges salakanyagai a verejt�kez�ssel egy�tt t�voznak a szervezetb�l, ez�ltal az infraszauna er�sen m�regtelen�t� hat�s�. Az infraszauna el�nye, hogy alacsonyabb h�m�rs�kleten �zemel, ez�ltal hosszabb ideig tart�zkodhatnak a szaun�ban az azt ig�nybevev�k.",
			"A j�gkamra funkci�ja, hogy leh�tse, felpezsd�tse a felhev�lt testet. A j�gkamr�t �ltal�ban szaun�z�s ut�n szok�s haszn�lni, amikor a j�g �rint�se �d�t�, kellemes hat�s�. A kamr�ban j�gk�sa van elhelyezve helyez�nk el, amivel a testet kell bed�rzs�lni. A j�gkamra haszn�lata jav�tja a v�rkering�st �s j�t�kony hat�ssal van a b�r sz�ps�g�re.",
			"Kering�si massz�zs - Neh�z l�bak syndroma kezel�s",
			"Az orosz szaun�ban a k�veket 60 Celsius fokra hev�tik �s �lland�an locsolj�k. �gy nedves, tr�pusi kl�m�hoz hasonl� a leveg�. �rdekess�ge, hogy a speci�lis kialak�t�s� szauna kabin fala far�nk�kb�l �p�l fel.",
			"A s�s leveg� j�t�kony �lettani hat�sa r�g�ta ismert. A s�b�ny�k kellemes leveg�je sz�les k�rben javasolt ter�pia l�g�ti megbeteged�sek, p�ld�ul asztma kezel�se c�lj�b�l. A s�kamr�k, illetve s�barlangok olyan mesters�ges helyis�gek, ahol a leveg� s�tartalma magas, ez�ltal az �lettani hat�sai j�t�konyak a l�gutakra. A kialak�tott s�kamr�ban a s�t�mb�k kip�rolg�sa 70-85%-os p�ratartalmat biztos�t. A s� kiszor�tja a leveg�b�l a k�ros anyagokat, a polleneket �s a port, �gy az itt bel�legzett leveg� tiszta �s �d�t� hat�s�. A s�kamr�k l�togat�sa naponta, 30-40 perc id�tartamban javasolt.",
			"A b�r intenz�v l�gz�s�t az elhalt h�msejtek elt�vol�t�s�t k�vet�en �rhetj�k el. A finom granul�tumokat tartalmaz� olaj alap� anyaggal t�rt�nik a test massz�roz�sa. A m�hviasz, valamint a z�ld alga m�r a massz�zs folyam�n seg�ti a b�r regener�l�d�s�t. A massz�zst k�vet�en a b�r puh�v�, selymess� �s hidrat�ltt� v�lik.",
			"A f�rd� t�vol-keleti centrum�ban v�gzett keleti friss�t�, regener�l� kezel�sek. A szem�lyre szabott kezel�sek t�pl�lkoz�si �s �letviteli tan�csad�st, olajos massz�zzsal t�rt�n� m�regtelen�t�st, fesz�lts�gold�st, gy�gyn�v�nyekkel, �sv�nyokkal, �s f�mekkel val� gy�gy�t�st foglalnak magukban.",
			"A modern kor wellness kincsest�r�nak r�sze a tepid�rium, m�s n�ven langyos meleg�t�. A tepid�rium olyan helyis�g, melynek h�m�rs�klete alig haladja meg az emberi test h�m�rs�klet�t, p�ratartalma ide�lisan 30-40% �s leveg�je illatanyagokkal d�s�tott. Rendszeres haszn�lat�val a sz�v �s �rrendszeri, illetve a megf�z�s jelleg� betegs�gek egyar�nt megel�zhet�ek. Fontos tulajdons�ga, hogy nem terheli a szervezetet, �gy a tepid�riumban korl�tlan ideig tart�zkodhatunk.",
			"A thai massz�zs egy �si, t�vol-keleti hagyom�nyokon nyugv� friss�t� �s regener�l� hat�s� massz�zs. A massz�zs sor�n nem az izmokat, hanem az energiavonalak ment�n tal�lhat� �lettani pontokat massz�rozz�k, k�zzel, k�ny�kkel, �s l�bbal, minden egyes mozdulatot finoman kezdve, majd egyre fokoz�d� intenzit�ssal. A kezel�seket thaif�ldi terapeut�k v�gzik.",
			"A thai massz�zs egy �si, t�vol-keleti hagyom�nyokon nyugv� friss�t� �s regener�l� hat�s� massz�zs. A massz�zs sor�n nem az izmokat, hanem az energiavonalak ment�n tal�lhat� �lettani pontokat massz�rozz�k, k�zzel, k�ny�kkel, �s l�bbal, minden egyes mozdulatot finoman kezdve, majd egyre fokoz�d� intenzit�ssal. A kezel�seket thaif�ldi terapeut�k v�gzik.",
			"A t�r�k pezsg�f�rd�ben a be�p�tett v�zsugarak enyh�n massz�rozz�k a testr�szeket, �l�nk�tve a kering�st. Ehhez hozz�j�rul, hogy a v�z pezsg�se, forg�sa felfriss�t �s hat�konyan relax�l.",
			"Az aromakabinok h�m�rs�klete 60-65 Celsius fok, a szaun�z�s itt aromaolajok haszn�lata mellett t�rt�nik. Att�l f�gg�en, hogy �gy milyen aromaolaj ker�l bel�legz�sre, lehet a szaun�z�s relax�l� vagy �ppen friss�t� hat�s� �s a kellemes melegnek k�sz�nhet�en old�dik a stressz is.",
			"Ayurv�da: �si eredete ellen�re sokkal �tfog�bb, mint a mai nyugati orvosl�s. Egy szakember nem csup�n a betegs�gmegel�z�sben, vagy a kr�nikus betegs�gek kezel�s�ben seg�thet, hanem harm�ni�ba hozhatja a teljes emberi �letet: testi �s lelki probl�m�kra egyar�nt megold�st ny�jthat. A keleti orvosl�sban nincs gy�gy�thatatlan betegs�g, a gy�gyul�sunk megfelel� m�dszerek, megfelel� motiv�ci�k k�rd�se, �s a szorgalmas, k�vetkezetes munk��.",
			"A barlangzuhany egy wellness elemekkel felszerelt hidromassz�zs zuhany kellemes k�rnyezetben, ami a v�z �rint�s�vel felpezsd�t, �d�t, massz�roz." };

	@PostConstruct
	public void init() {
		EntityManager em = emf.createEntityManager();

		User user = new User();
		user.setUsername("admin");
		user.setPassword("admin");
		user.setTotalPoints(700L);
		em.persist(user);

		Random rnd = new Random();

		for (int i = 0; i < titles.length; i++) {
			Service s1 = new Service();
			s1.setTitle(titles[i]);
			s1.setDescription(descs[i]);
			s1.setCost((rnd.nextInt(100) + 1) * 10);
			em.persist(s1);
		}

		em.close();
	}
}
