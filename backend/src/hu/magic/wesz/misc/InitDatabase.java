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

	private static final String[] titles = { "Algás tengeri fürdõ",
			"Bioszauna", "Finn szauna merülõmedencével", "Gõzfürdõ",
			"Infraszauna", "Jégkamra", "Keringési masszázs", "Orosz szauna",
			"Sókamra", "Spa algás bõrradír", "Távolkeleti terápia",
			"Tepidárium", "Thai gyógynövényes masszázs", "Thai masszázs",
			"Török pezsgõfürdõ", "Aromakabin", "Ayurvéda - Indiai fejmasszázs",
			"Barlangzuhany" };

	private static final String[] descs = {
			"A tengeri alga fokozza az anyagcserét és a vérkeringést. Az alga magas ásványi anyag tartalmának, valamint a hidromasszázs fizikai erejének köszönhetõen a korábban elveszített ásványi anyagok pótlódnak a szervezetben. Az alga keringésfokozó, zsíroldó hatású, ezért a fürdõ méregtelenít és karcsúsít.",
			"Kellemes, 40-50 Celsius fokos hõmérsékletû szauna, amely nem megterhelõ a szervezet számára.",
			"Lényege, hogy a szaunát használó személy teste felhevüljön, izzadjon, ezáltal méregtelenítsen, valamint a testi-lelki pihenés.",
			"Ha valakinek túl magas hõfokot jelent a finn szauna, relaxálhat a gõzfürdõben is, melynek hõmérséklete alacsonyabb, kb. 43 Celsius fok, páratartalma viszont magasabb (100%), az izzadás ezért itt is garantált. Mivel a test fokozatosan hevül fel, nem terheli meg a szervezetet, így a keringési problémákkal küszködõknek is ajánlott.",
			"Az infraszauna nevébõl adódóan más technikával mûködik, mint a hagyományos, finn szauna, helyiségét infrasugarakkal fûtik, amik a fény láthatatlan tartományába tartoznak. Az infrasugarak belülrõl melegítik a testet, tágítják az ereket, serkentik a vérkeringést, illetve a szervezet felesleges salakanyagai a verejtékezéssel együtt távoznak a szervezetbõl, ezáltal az infraszauna erõsen méregtelenítõ hatású. Az infraszauna elõnye, hogy alacsonyabb hõmérsékleten üzemel, ezáltal hosszabb ideig tartózkodhatnak a szaunában az azt igénybevevõk.",
			"A jégkamra funkciója, hogy lehûtse, felpezsdítse a felhevült testet. A jégkamrát általában szaunázás után szokás használni, amikor a jég érintése üdítõ, kellemes hatású. A kamrában jégkása van elhelyezve helyezünk el, amivel a testet kell bedörzsölni. A jégkamra használata javítja a vérkeringést és jótékony hatással van a bõr szépségére.",
			"Keringési masszázs - Nehéz lábak syndroma kezelés",
			"Az orosz szaunában a köveket 60 Celsius fokra hevítik és állandóan locsolják. Így nedves, trópusi klímához hasonló a levegõ. Érdekessége, hogy a speciális kialakítású szauna kabin fala farönkökbõl épül fel.",
			"A sós levegõ jótékony élettani hatása régóta ismert. A sóbányák kellemes levegõje széles körben javasolt terápia légúti megbetegedések, például asztma kezelése céljából. A sókamrák, illetve sóbarlangok olyan mesterséges helyiségek, ahol a levegõ sótartalma magas, ezáltal az élettani hatásai jótékonyak a légutakra. A kialakított sókamrában a sótömbök kipárolgása 70-85%-os páratartalmat biztosít. A só kiszorítja a levegõbõl a káros anyagokat, a polleneket és a port, így az itt belélegzett levegõ tiszta és üdítõ hatású. A sókamrák látogatása naponta, 30-40 perc idõtartamban javasolt.",
			"A bõr intenzív légzését az elhalt hámsejtek eltávolítását követõen érhetjük el. A finom granulátumokat tartalmazó olaj alapú anyaggal történik a test masszírozása. A méhviasz, valamint a zöld alga már a masszázs folyamán segíti a bõr regenerálódását. A masszázst követõen a bõr puhává, selymessé és hidratálttá válik.",
			"A fürdõ távol-keleti centrumában végzett keleti frissítõ, regeneráló kezelések. A személyre szabott kezelések táplálkozási és életviteli tanácsadást, olajos masszázzsal történõ méregtelenítést, feszültségoldást, gyógynövényekkel, ásványokkal, és fémekkel való gyógyítást foglalnak magukban.",
			"A modern kor wellness kincsestárának része a tepidárium, más néven langyos melegítõ. A tepidárium olyan helyiség, melynek hõmérséklete alig haladja meg az emberi test hõmérsékletét, páratartalma ideálisan 30-40% és levegõje illatanyagokkal dúsított. Rendszeres használatával a szív és érrendszeri, illetve a megfázás jellegû betegségek egyaránt megelõzhetõek. Fontos tulajdonsága, hogy nem terheli a szervezetet, így a tepidáriumban korlátlan ideig tartózkodhatunk.",
			"A thai masszázs egy õsi, távol-keleti hagyományokon nyugvó frissítõ és regeneráló hatású masszázs. A masszázs során nem az izmokat, hanem az energiavonalak mentén található élettani pontokat masszírozzák, kézzel, könyökkel, és lábbal, minden egyes mozdulatot finoman kezdve, majd egyre fokozódó intenzitással. A kezeléseket thaiföldi terapeuták végzik.",
			"A thai masszázs egy õsi, távol-keleti hagyományokon nyugvó frissítõ és regeneráló hatású masszázs. A masszázs során nem az izmokat, hanem az energiavonalak mentén található élettani pontokat masszírozzák, kézzel, könyökkel, és lábbal, minden egyes mozdulatot finoman kezdve, majd egyre fokozódó intenzitással. A kezeléseket thaiföldi terapeuták végzik.",
			"A török pezsgõfürdõben a beépített vízsugarak enyhén masszírozzák a testrészeket, élénkítve a keringést. Ehhez hozzájárul, hogy a víz pezsgése, forgása felfrissít és hatékonyan relaxál.",
			"Az aromakabinok hõmérséklete 60-65 Celsius fok, a szaunázás itt aromaolajok használata mellett történik. Attól függõen, hogy így milyen aromaolaj kerül belélegzésre, lehet a szaunázás relaxáló vagy éppen frissítõ hatású és a kellemes melegnek köszönhetõen oldódik a stressz is.",
			"Ayurvéda: õsi eredete ellenére sokkal átfogóbb, mint a mai nyugati orvoslás. Egy szakember nem csupán a betegségmegelõzésben, vagy a krónikus betegségek kezelésében segíthet, hanem harmóniába hozhatja a teljes emberi életet: testi és lelki problémákra egyaránt megoldást nyújthat. A keleti orvoslásban nincs gyógyíthatatlan betegség, a gyógyulásunk megfelelõ módszerek, megfelelõ motivációk kérdése, és a szorgalmas, következetes munkáé.",
			"A barlangzuhany egy wellness elemekkel felszerelt hidromasszázs zuhany kellemes környezetben, ami a víz érintésével felpezsdít, üdít, masszíroz." };

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
