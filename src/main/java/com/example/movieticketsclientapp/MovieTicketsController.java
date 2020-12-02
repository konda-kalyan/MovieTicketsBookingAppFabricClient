package com.example.movieticketsclientapp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.validation.Valid;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallet.Identity;
import org.hyperledger.fabric.gateway.impl.WalletIdentity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.NetworkConfig;
import org.hyperledger.fabric.sdk.NetworkConfig.OrgInfo;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.RegistrationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/****** REST Controller for Movie Tickets App *******/

@RestController(value = "/")
public class MovieTicketsController {
	
	public MovieTicketsController() {
		
		// ***** Do all initial setup to invoke/interact with chaincodes/contracts
		setup();
	}
	
	// Initializes the Ledger with the custom values we provide
	@PostMapping("initledger")
	public ResponseEntity<String> initLedger(@Valid @RequestBody InitLedgerRequest initLedgerRequest)
			throws JsonProcessingException {
		
		try {
			contract.submitTransaction("init", Integer.toString(initLedgerRequest.getTheatresCount()), Integer.toString(initLedgerRequest.getScreensCount()), Integer.toString(initLedgerRequest.getShowsCount()), Integer.toString(initLedgerRequest.getMaxTicketPerShow()), Integer.toString(initLedgerRequest.getMaxSodasAvailable()) 
); // **** Chaincode call
		} catch (ContractException | TimeoutException | InterruptedException e1) {
			return new ResponseEntity<String>("Failed", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>("Success", HttpStatus.OK);
	} // initLedger REST ends
	
	
	@PostMapping("issuetickets")
	public ResponseEntity<IssueTicketsResponse> IssueTickets(@Valid @RequestBody TicketsRequest ticketsRquest)
			throws JsonProcessingException {

		if(ticketsRquest.getShowDate() == null) { // If client doesn't provide date means, we are assuming that buyer wants to book tickets for today
			ticketsRquest.setShowDate(new Date());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		String requestString = mapper.writeValueAsString(ticketsRquest);

		byte[] issueTicketsCCResult = null;
		
		try {
			// if tickets are booked, then, ChainCode returns tickets info
			issueTicketsCCResult = contract.submitTransaction("IssueTickets", requestString); // **** Chaincode call
		} catch (ContractException | TimeoutException | InterruptedException e1) {
			e1.printStackTrace();
			IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.name(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"There was a problem while executing ChainCode.",
					new TicketInfo());

			return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String issueTicketsCCResultString = new String(issueTicketsCCResult, StandardCharsets.UTF_8);
		ObjectMapper mapper1 = new ObjectMapper();
		TicketInfo ticketInfo = mapper1.readValue(issueTicketsCCResultString, TicketInfo.class);
		
		if(ticketInfo.getTicketNumber() == null) {	// requested number of tickets are not available and hence Blockchain CC has send empty response.
			IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
					HttpStatus.BAD_REQUEST.name(),
					HttpStatus.BAD_REQUEST.value(),
					"Tickets are not available.",
					ticketInfo);

			return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.BAD_REQUEST);
		}
		
		IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
				HttpStatus.OK.name(),
				HttpStatus.OK.value(),
				"Tickets are issued successfully.",
				ticketInfo);

		return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.OK);
		
	} // IssueTickets REST ends

	
	@PostMapping("getnoofavailabletickets")
	public ResponseEntity<Integer> GetNoOfAvailableTickets(@Valid @RequestBody TicketsAvailabilityRequest ticketsAvailabilityRequest) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		String requestString = mapper.writeValueAsString(ticketsAvailabilityRequest);
		
		byte[] getNoOfAvailableTicketsCCResult = null;
		
		try {
			getNoOfAvailableTicketsCCResult = contract.submitTransaction("GetNoOfAvailableTickets", requestString); // **** Chaincode call
		} catch (ContractException | TimeoutException | InterruptedException e1) {
			
			return new ResponseEntity<Integer>(-0, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Integer noOfAvailableTickets = Integer.parseInt(new String(getNoOfAvailableTicketsCCResult, StandardCharsets.UTF_8));
				
		return new ResponseEntity<Integer>(noOfAvailableTickets, HttpStatus.OK);
		
	} // getNoOfAvailableTickets REST ends
	
	
	@PutMapping("grabwaterbottleandpopcorn")
	public ResponseEntity<IssueTicketsResponse> GrabWaterBottleAndPopcorn(@Valid @RequestBody String ticketNumber) throws JsonProcessingException {
			
		byte[] grapWaterCCResult = null;
		
		try {
			grapWaterCCResult = contract.submitTransaction("GrabWaterBottleAndPopcorn", ticketNumber); // **** Chaincode call
		} catch (ContractException | TimeoutException | InterruptedException e1) {
			e1.printStackTrace();
			IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.name(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"There was a problem while executing ChainCode.",
					new TicketInfo());

			return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String grapWaterCCResultCCResultString = new String(grapWaterCCResult, StandardCharsets.UTF_8);
		ObjectMapper mapper1 = new ObjectMapper();
		TicketInfo ticketInfo = mapper1.readValue(grapWaterCCResultCCResultString, TicketInfo.class);
		
		if(ticketInfo.getRandomNumberForSodaExchange() == null) {	// means, client has passed ticket number or tried to grab for future date ticket
			IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
					HttpStatus.BAD_REQUEST.name(),
					HttpStatus.BAD_REQUEST.value(),
					"Ticket number is invalid or tried to grab for future date ticket.",
					ticketInfo);

			return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.BAD_REQUEST);
		}
		
		IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
				HttpStatus.OK.name(),
				HttpStatus.OK.value(),
				"Water bottle and Popcorn has been given successfully.",
				ticketInfo);

		return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.OK);
		
	} // GrabWaterBottleAndPopcorn REST ends
	
	
	@PutMapping("exchangewithsoda")
	public ResponseEntity<IssueTicketsResponse> ExchangeWithSoda(@Valid @RequestBody String ticketNumber) throws JsonProcessingException {
		
		byte[] exchangeSodaCCResult = null;
		
		try {
			exchangeSodaCCResult = contract.submitTransaction("ExchangeWithSoda", ticketNumber); // **** Chaincode call
		} catch (ContractException | TimeoutException | InterruptedException e1) {
			e1.printStackTrace();
			IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
					HttpStatus.INTERNAL_SERVER_ERROR.name(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					"There was a problem while executing ChainCode.",
					new TicketInfo());

			return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		String exchangeSodaCCResultCCResultString = new String(exchangeSodaCCResult, StandardCharsets.UTF_8);
		ObjectMapper mapper1 = new ObjectMapper();
		TicketInfo updatedTicketInfo = mapper1.readValue(exchangeSodaCCResultCCResultString, TicketInfo.class);
		
		if(updatedTicketInfo.getNumberOfSodasExchanged() == null) {	// means, client has passed ticket number which doesn't exists.
			IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
					HttpStatus.BAD_REQUEST.name(),
					HttpStatus.BAD_REQUEST.value(),
					"Ticket number is invalid. Or, not eligible to exchange with Soda.",
					updatedTicketInfo);

			return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.BAD_REQUEST);
		}
		
		IssueTicketsResponse issueTicketsResponse = new IssueTicketsResponse(
				HttpStatus.OK.name(),
				HttpStatus.OK.value(),
				"Water bottle has been exchanged with Soda successfully.",
				updatedTicketInfo);

		return new ResponseEntity<IssueTicketsResponse>(issueTicketsResponse, HttpStatus.OK);
		
	} // ExchangeWithSoda REST ends
	
	
	// Gets number of Sodas are left for today
	// this API doesn't need any inputs
	@GetMapping("getnoofavailablesodas")
	public ResponseEntity<Integer> GetNoOfAvailableSodas() throws JsonProcessingException {
		
		byte[] getNoOfAvailableSodasCCResult = null;
		
		try {
			getNoOfAvailableSodasCCResult = contract.submitTransaction("GetNoOfAvailableSodas"); // **** Chaincode call
		} catch (ContractException | TimeoutException | InterruptedException e1) {
			
			return new ResponseEntity<Integer>(-0, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Integer noOfAvailableSodas = Integer.parseInt(new String(getNoOfAvailableSodasCCResult, StandardCharsets.UTF_8));
				
		return new ResponseEntity<Integer>(noOfAvailableSodas, HttpStatus.OK);
		
	} // GetNoOfAvailableSodas REST ends
	
	
	
	
	Contract contract = null;
	Gateway.Builder builder = null;
	Network network = null;
	Gateway gateway = null;
	NetworkConfig networkConfig = null;

	// ***** Do all initial setup to invoke/interact with chaincodes/contracts
	private void setup() {
		String networkConfigFilePath = "./network-config-tls.yaml";

		try {
			networkConfig = NetworkConfig.fromYamlFile(new File(networkConfigFilePath));
		} catch (InvalidArgumentException | NetworkConfigurationException | IOException e2) {
			e2.printStackTrace();
			throw new ClientChainCodeException(e2.getMessage());
		}

		OrgInfo clientOrg = networkConfig.getClientOrganization();
		NetworkConfig.CAInfo clientOrgCAInfo = clientOrg.getCertificateAuthorities().get(0);

		HFCAClient hfcaClient = null;
		try {
			hfcaClient = HFCAClient.createNewInstance(clientOrgCAInfo.getCAName(), clientOrgCAInfo.getUrl(),
					clientOrgCAInfo.getProperties());
		} catch (MalformedURLException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e2) {
			e2.printStackTrace();
			throw new ClientChainCodeException(e2.getMessage());
		}

		CryptoSuite cryptoSuite = null;
		try {
			cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		} catch (IllegalAccessException | InstantiationException | ClassNotFoundException | CryptoException
				| InvalidArgumentException | NoSuchMethodException | InvocationTargetException e2) {
			e2.printStackTrace();
			throw new ClientChainCodeException(e2.getMessage());
		}
		
		hfcaClient.setCryptoSuite(cryptoSuite);

		// getting Client Org CA Registrars info (admin info)
		NetworkConfig.UserInfo registrarInfo = clientOrg.getCertificateAuthorities().get(0).getRegistrars().iterator()
				.next();

		String afficiation = "org1.department1";
		registrarInfo.setAffiliation("org1.department1");
		Enrollment adminEnrollment = null;
		try {
			adminEnrollment = hfcaClient.enroll(registrarInfo.getName(), registrarInfo.getEnrollSecret());
		} catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e2) {
			e2.printStackTrace();
			throw new ClientChainCodeException(e2.getMessage());
		}
		
		registrarInfo.setEnrollment(adminEnrollment);

		UserContext user1 = new UserContext();
		Date date = new Date();
		// This method returns the time in milli seconds
		long currentTimeInMilliSecs = date.getTime();
		String uniqueUserName = "User" + currentTimeInMilliSecs;
		user1.setName(uniqueUserName);
		user1.setMspId(clientOrg.getMspId());
		user1.setAffiliation(afficiation);
		Set<String> roles = new HashSet<String>();
		roles.add("client");
		user1.setRoles(roles);

		RegistrationRequest rr = null;
		try {
			rr = new RegistrationRequest(user1.getName(), user1.getAffiliation());
		} catch (Exception e1) {
			// TODO: Should take proper action here
			e1.printStackTrace();
		}
		
		String enrollmentSecret = null;
		try {
			enrollmentSecret = hfcaClient.register(rr, registrarInfo);
		} catch (RegistrationException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e2) {
			e2.printStackTrace();
			throw new ClientChainCodeException(e2.getMessage());
		}

		Enrollment userEnrollment = null;
		try {
			userEnrollment = hfcaClient.enroll(user1.getName(), enrollmentSecret);
		} catch (EnrollmentException | org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException e2) {
			e2.printStackTrace();
			throw new ClientChainCodeException(e2.getMessage());
		}
		
		user1.setEnrollment(userEnrollment);
		
		// Load an existing wallet holding identities used to access the network.
		// First get the User Identity info that will be used to trigger transaction
		Path walletsDir = Paths.get("wallet");
		Wallet userWallet = null;
		try {
			userWallet = Wallet.createFileSystemWallet(walletsDir);
		} catch (IOException e1) {
			// TODO: Should take proper action here
			e1.printStackTrace();
		}
		
		Identity identity1 = new WalletIdentity("Org1MSP", userEnrollment.getCert(), userEnrollment.getKey());
		try {
			userWallet.put("user1", identity1);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ClientChainCodeException(e1.getMessage());
		}

		// Path to a common connection profile describing the network.
		Path networkConfigFile = Paths.get(networkConfigFilePath);

		// Configure the gateway connection used to access the network.
		builder = null;
		try {
			builder = Gateway.createBuilder().identity(userWallet, "user1").networkConfig(networkConfigFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new ClientChainCodeException(e1.getMessage());
		}

		// Create a gateway connection
		try {
			gateway = builder.connect();

			// Obtain a smart contract deployed on the network.
			network = gateway.getNetwork("mychannel");
			contract = network.getContract("mycc", "movieticketsapp"); // chaincode id and contract name
		} catch (Exception e) {
			e.printStackTrace();
			throw new ClientChainCodeException(e.getMessage());
		}
	} // setup ends
	
} // class ends