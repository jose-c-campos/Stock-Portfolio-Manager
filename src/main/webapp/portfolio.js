document.addEventListener('DOMContentLoaded', function() {
	
	const username = localStorage.getItem('user');
	if (username) {
		getBalance(username);
		getStockInfo(username);
	}
});


document.getElementById('stockInfo').addEventListener('submit', function(event) {
	event.preventDefault();
	submitTrade(stockTikcer);
});

function getBalance(username) {
    
    let url = new URL(window.location.origin + `/Assignment_4/BalanceServlet`); 
  
    url.searchParams.append('username', username);
    
    console.log(url); 

	fetch(url)
		.then(response => {
			if(!response.ok) {
				throw new Error('Network response not ok');
			}
			return response.json();
		})
		.then(data => {
			displayBalance(data);
		})
		.catch(error => console.error('Failed to fetch data:', error));
}

function displayBalance(data) {
	document.getElementById('cashBalance').innerText = `${parseFloat(data.balance).toFixed(2)}`;
	document.getElementById('accountValue').innerText = parseFloat(50000).toFixed(2);
}


function setUpPortfolio(data) {
	// Retrieve stocks from user Portfolio
	const container = document.getElementById('stockContainer');
	
	console.log('numStocks: ' + Object.keys(data).length)
	
	if (data.error || Object.keys(data).length === 0) {
		container.style.display = 'none';
		container.innertHTML = '<p>' + data.error + '</p>';
	}
	else {
		container.style.display = 'block'
		container.innerHTML = '';
	}
	
	
	Object.keys(data).forEach(stockTicker => {
		const stock = data[stockTicker];
		const stockDiv = document.createElement('div');
		stockDiv.className = 'stockInfo';
		
		
		const htmlContent = `
			<div class = "stockHeader">
				<h5 class = "stock-name">${stockTicker}  </h5>
				<h5 class = "stock-name" id = "company"> - ${stock.companyName}</h5>
			</div>
			<div>
				<table id = "table1">
					<tr>
						<th>Quantity: </th> <td id = "currNumStocks_${stockTicker}">${stock.quantity}</td>
					</tr>
					<tr>
						<th>Avg. Cost/Share </th> <td>${parseFloat(stock.avgCostPerShare).toFixed(2)}</td>
					</tr>
					<tr>
						<th>Total Cost: </th> <td>${parseFloat(stock.totalCost).toFixed(2)}</td>
					</tr>
				</table>
				<table id = "table2">
					<tr>
						<th>Change: </th> <td id = "change_${stockTicker}">${parseFloat(stock.change).toFixed(2)}</td>
					</tr>
					<tr>
						<th>Current Price: </th> <td>${parseFloat(stock.currentPrice).toFixed(2)}</td>
					</tr>
					<tr>
						<th>Market Value: </th> <td>${parseFloat(stock.marketValue).toFixed(2)}</td>
					</tr>
				</table>
			<fieldset>
				<div>
					<label for = "quantity_${stockTicker}">Quantity: </label>
					<input type = "text" id = "quantity_${stockTicker}" name = "quantity_${stockTicker}">
				</div>
				<div>
					<input type="radio" id="buy_${stockTicker}" name="buy_sell_${stockTicker}" value="buy" required> 
		        	<label for="buy_${stockTicker}">BUY</label>
		        	<input type="radio" id="sell_${stockTicker}" name="buy_sell_${stockTicker}" value="sell" checked>
		        	<label for="sell_${stockTicker}">SELL</label><br>
		    	</div>
		    	<div>
		        	<button type = "submit" id = "submitButton" onClick = "submitTrade('${stockTicker}');">Submit</button>
		       </div>
			</fieldset>
		</div>
		`;
		
		stockDiv.innerHTML = htmlContent;
		container.appendChild(stockDiv);
		
	});
	
	
	Object.keys(data).forEach(stockTicker => {
		const stock = data[stockTicker];
		const stockDiv = document.createElement('div');
		stockDiv.className = 'stockInfo';
	
		const change = `${stock.change}`;
		console.log('change', change);
		if (change > 0) {
			let icon = '<i class="fas fa-caret-up"></i>';
			document.getElementById(`change_${stockTicker}`).innerHTML = `${icon} ${change}`;
			document.getElementById(`change_${stockTicker}`).style.color = 'green';
		}
		else {
			let icon = '<i class="fas fa-caret-down"></i>';
			document.getElementById(`change_${stockTicker}`).innerHTML = `${icon} ${change}`;
			document.getElementById(`change_${stockTicker}`).style.color = 'red';
		}
	});
	
	
}

function getStockInfo(username) {
	let url = new URL(window.location.origin + `/Assignment_4/portfolioServlet`); 
	url.searchParams.append('username', username);
	
	console.log(url); 
	
	fetch(url)
		.then(response => {
			if(!response.ok) {
				throw new Error('Network response not ok');
			}
			return response.json();
		})
		.then(data => {
			setUpPortfolio(data);
		})
		.catch(error => console.error('Failed to fetch data:', error));
}

 function logout() {
	localStorage.clear();
	window.location.href = 'index.html';
 }
 
 function getMarketStatus() {
	const now = new Date();
	const hours = padZero(now.getHours());
	const minutes = padZero(now.getMinutes());
	const currTime = hours + minutes;
	
	console.log(currTime);
	
	const marketOpen = 630;
	const marketClose = 1300;
	
	if ( (currTime >= marketOpen) && (currTime < marketClose) ) {
		return true; 
	}
	else {
		return false;
	}
}

function padZero(num) {
    return num < 10 ? '0' + num : num.toString();
}
 
 function submitTrade(stockTicker) {
	
	if (!getMarketStatus()) {
		alert('Market is closed, cannot execute trades');
		return; 
	}
	
	const quantity = document.getElementById(`quantity_${stockTicker}`).value;
	const buySellChoice = (document.querySelector(`input[name='buy_sell_${stockTicker}']:checked`)).value;
	const username = localStorage.getItem('user');
	
	console.log('Stock Ticker: ', stockTicker);
	console.log('Quantity: ', quantity);
	console.log('Buy/Sell: ', buySellChoice);
	console.log('username: ', username);
	
	
	// Prevent selling more stocks than you own
	if ( (buySellChoice == 'sell') && (quantity > document.getElementById(`currNumStocks_${stockTicker}`).value) ) {
		alert('FAILED, transaction not possible');
		return;
	}
	
	let url = new URL(window.location.origin + `/Assignment_4/TradeServlet`); 
  
    url.searchParams.append('stockTicker', stockTicker);
    url.searchParams.append('quantity', quantity);
    url.searchParams.append('buySellChoice', buySellChoice);
    url.searchParams.append('username', username);
    
    console.log(url); 

	fetch(url)
		.then(response => {
			if(!response.ok) {
				throw new Error('Network response not ok');
			}
			return response.json();
		})
		.then(data => {
			reportTrade(stockTicker, quantity, data, buySellChoice);
		})
		.catch(error => console.error('Failed to fetch data:', error));
 }
 
 function reportTrade(stockTicker, quantity, data, buySellChoice) {
	
	if (!data) {
		alert('FAILED, transaction not possible');
		return;
	}
	if (buySellChoice == 'buy') {
		alert('Bought ' + quantity + ' shares of ' + stockTicker + ' for $' + parseFloat(data.result).toFixed(2));
	}
	else {
		alert('Sold ' + quantity + ' shares of ' + stockTicker + ' for $' + parseFloat(data.result).toFixed(2));
	}
	window.location.href = 'portfolio.html';
	return;
 }