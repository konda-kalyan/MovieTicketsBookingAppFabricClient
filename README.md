## Fabric Client application for "Movie Tickets Booking Application"

This is developed using <b>fabric-gateway-java</b> SDK and <b>Java Springboot</b> to expose REST APIs.

<b>CouchDB</b> has used as State Database

Chaincode application is available at "https://github.com/konda-kalyan/MovieTicketsBookingAppChainCode".

This application has been tested on fabric 1.4.4 network by using sample first_network (https://github.com/hyperledger/fabric-samples/tree/release-1.4/first-network)

### Assumptions:
* Buyer can book any number of tickets at a time. Not limiting to book only one ticket per request.
* Though tickets can be booked for future date, issuing Water bottles and Sodas exchange is allowed only on show date. 
* If date is not mentioned then, by default, tickets are booked for today.
* DONT allow partial number of tickets booking. Means, let's say buyer has asked for 10 tickets and only 5 tickets are available then don't allow to book tickets.
* ALLOW partial number of sodas exchangewed. Means, let's say buyer has booked 10 tickets and come for Sodas exchange and only 5 sodas are available then allow buyer to exchange 5 sodas.

### Implementation Highlights and Business Logic Used:
* Solution is scalable for any number of theatres, screens, shows and tickets
* Ticket number is generated using current time stamp
* Issuing Water bottles, Pop-corn and exchanging Sodas are allowed for only Show Date.
* Whoever got even random number are eligible for Soda exchange
* Partial number of Sodas can be exchanged.
* Tickets can be booked for today or even future date. If date is not asked, then default is today.
* At a time, any number of tickets can be booked.
* Booking partial number of tickets are not allowed.

### Improvements can be done:
* Movie details are not captured
* Seat numbers not considered
* Should not allow to book for past date
* Security should be added

### Prerequisites:

* Docker and Docker-Compose
* Git
* Java 1.8

### Setup:
* Setup fabric 1.4.4 network:
  Follow steps mentioned [here](https://hyperledger-fabric.readthedocs.io/en/release-1.4/install.html).
	Replace two scripts: first-network/scripts.sh and first-network/utils.sh with scripts available at [Chaincode Repo](https://github.com/konda-kalyan/MovieTicketsBookingAppChainCode/tree/main/scripts)
* Clone Chaincodes repository [from](https://github.com/konda-kalyan/MovieTicketsBookingAppChainCode) and place in 'fabric-samples/chaincode' directory
* Bring up the fabric network:
```
	cd fabric-samples/first-network
	byfn.sh -s couchdb -l java -i 1.4.4 -a		(Note that we have choosen CouchDB as StateDB and Java as Chaincode language)
  ```
* Clone and Run the Client Application
 ```
	git clone https://github.com/konda-kalyan/MovieTicketsBookingAppFabricClient
	cd MovieTicketsBookingAppFabricClient
	mvn spring-boot:run   (note that this application runs at default port: 8080)
   ```
* Test the end to end functionality by invoking REST APIs that are exposed via Client app.
	You can use Postman to do so.
	API end points, payloads and test cases are mentioned below and also checked in [here](https://github.com/konda-kalyan/MovieTicketsBookingAppFabricClient/blob/main/MovieTicketsBooking-Testcases-Payloads.docx)



### Testcases & Payloads
<b>API - 1 : Initialize Ledger</b> with custom initial values for number of theatres, screens, shows, number of tickets per show, max Sodas available

  + REST API endpoint and Payloads
	
  GET	-> ip-address:8080/initledger

	Request
		{
			"theatresCount": 10,
			"screensCount": 7,
			"showsCount": 5,
			"maxTicketPerShow": 150,
			"maxSodasAvailable": 250
		}
	
	Response
		“Success” or “Failure”

Positive testcases
1.	Set above mentioned initial parameters with custom values and verify that Ledger has been updated with these values. Can be checked in CouchDB as follows:

Negative testcases
1.	Try with wrong inputs and see that application throw error

<b>API - 2 : Issue Tickets</b>

  + REST API endpoint and Payloads
	
POST -> ip-address:8080/issuetickets

	Request
		{
			"theatre": "theatre2",
			"screen": "screen1",
			"show": "show3",
			"buyerName": "Konda",
			"numberOfTicketsBuying": 20,
			"showDate": "2020-12-02"
		}
	
	Response
	{
    "status": "OK",
    "statusCode": 200,
    "message": "Tickets are issued successfully.",
    "ticketInfo": {
        "ticketNumber": "20201202145505513",
        "buyerName": "Konda",
        "numberOfTicketsBooked": 20,
        "randomNumberForSodaExchange": null,
        "exchangedWithSoda": false,
        "numberOfSodasExchanged": null,
        "showDate": "2020-12-02"
    }
}

Positive testcases
1.	Book one ticket.
2.	Book more than one ticket at one shot (in a request).
3.	Book tickets for future date (not today)
4.	Try without providing showDate info, tickets should be booked for today.
5.	Special case: let’s say, buyer has asked for 10 tickets, but only 5 tickets are available. (in this case, we are not allowing partial bookings)
Negative testcases
1.	Try to book tickets for the show for which tickets are not available
2.	Try to book for past date (not implement due to time constaint)
3.	Get the tickets availability info for given theatre, screen, show and date

<b>API - 3 : Get the tickets availability info for given theatre, screen, show and date</b>

  + REST API endpoint and Payloads
	
POST ->	ip-address:8080/getnoofavailabletickets

	Request
		{
			"theatre": "theatre2",
			"screen": "screen1",
			"show": "show3",
			"showDate": "2020-12-02"
		}
	
	Response
	<number of available tickets. 0, if tickets are not available>

Positive testcases
1.	Try for the show for which not even single ticket is booked
2.	Try for the show for which some tickets are booked
Negative testcases
1.	Try for the show for which max allowed tickets are booked
4.	Grab Water bottle and Popcorn

<b>API - 4 : Grab Water bottle and Popcorn</b> 

  + REST API endpoint and Payloads
	
PUT	-> ip-address:8080/grabwaterbottleandpopcorn

	Request
		<Ticket number in request body>
	
	Response
	{
    "status": "OK",
    "statusCode": 200,
    "message": "Water bottle and Popcorn has been given successfully.",
    "ticketInfo": {
        "ticketNumber": "20201202145505513",
        "buyerName": "Konda",
        "numberOfTicketsBooked": 20,
        "randomNumberForSodaExchange": 231877564,
        "exchangedWithSoda": false,
        "numberOfSodasExchanged": null,
        "showDate": "2020-12-02"
    }
}

Positive testcases
1.	Try to grab the water bottle and popcorn
Negative testcases
1.	Try to grab for the ticket which is booked for future date (not today) (we are allowing if and only if their show date is today)
2.	Try with invalid ticket number
5.	Exchange Water bottle with Soda

<b>API - 5 : Exchange Water bottle with Soda</b> 

  + REST API endpoint and Payloads
	
PUT	-> ip-address:8080/exchangewithsoda

	Request
		<Ticket number in request body>
	
	Response
	{
    		"status": "OK",
    		"statusCode": 200,
  		"message": "Water bottle has been exchanged with Soda successfully.",
   		"ticketInfo": {
        			"ticketNumber": "20201202145505513",
        			"buyerName": "Konda",
        			"numberOfTicketsBooked": 20,
        			"randomNumberForSodaExchange": 231877564,
        			"exchangedWithSoda": true,
        			"numberOfSodasExchanged": 20,
        			"showDate": "2020-12-02"
		}
    		}

Positive testcases
1.	Try to exchange the water bottle with Soda (for which even random number has been generated) – allowed to exchange
2.	Try to exchange the water bottle with Soda (for which odd random number has been generated) – NOT allowed to exchange
3.	Special case: let’s say, buyer has asked for 10 tickets, but only 5 tickets are available. (in this case, we are not allowing partial bookings)
Negative testcases
1.	Try to exchange when Sodas are not available
2.	Try to grab for the ticket which is booked for future date (not today) (we are allowing if and only if their show date is today)
3.	Try with invalid ticket number
6.	Get the Sodas availability info for today

<b>API - 6 : Get the Sodas availability info for today</b> 
  + REST API endpoint and Payloads
	
GET	-> ip-address:8080/getnoofavailablesodas

	Request
		<nothing>
	
	Response
	<number of available tickets. 0, if tickets are not available>

Positive testcases
1.	Try to get number of Sodas remaining for today
Negative testcases
