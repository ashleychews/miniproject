package vttp.ssf.sg.miniproject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import vttp.ssf.sg.miniproject.models.Attractions;
import vttp.ssf.sg.miniproject.services.AttractionsService;
import vttp.ssf.sg.miniproject.services.MediaService;

@SpringBootApplication
public class MiniprojectApplication implements CommandLineRunner{

	@Autowired
	private AttractionsService attSvc;

	@Autowired
	private MediaService mdSvc;

	public static void main(String[] args) {
		SpringApplication.run(MiniprojectApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {


		//for checking
		//String url = attSvc.getAttractions("singapore");
		//System.out.println("url:"+url);
		//output:https://api.stb.gov.sg/content/attractions/v2/search?searchType=keyword&searchValues=singapore
		
		List<Attractions> attractions = attSvc.getAttractions("park");
		System.out.println("attractions" + attractions);
		
		// Attractions attractions = attSvc.getAttractionDetailsByUUID("002267aa36fc6314ad49266abf08adef735");
		// System.out.println("attractions" + attractions);

		String url = mdSvc.getMediaUrl("1010a2a130a0ae24afd9b02678a051c9a5a");
		System.out.println("url" + url);

		Attractions details = attSvc.getAttractionDetailsByUUID("002267aa36fc6314ad49266abf08adef735");



	}
	
}
