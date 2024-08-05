document.addEventListener('DOMContentLoaded', function() {
	const username = localStorage.getItem('user');
	if (username) {
		updateDateTime();
		setLoginPage();
	}
});

document.getElementById('searchForm').addEventListener('submit', function(event) {
	event.preventDefault();
	searchStock();
});

function setLoginPage() {
	 document.getElementById('login').style.display = 'none';
	 document.getElementById('home').style.right = '130px';
	 document.getElementById('portfolio').style.display = 'inline-block';
	 document.getElementById('logout').style.display = 'inline-block';
	  document.getElementById('portfolio').style.right = '70px';
}

function logout() {
	localStorage.clear();
	window.location.href = 'index.html';
}

async function searchStock() {
	let stockTicker = document.getElementById('stockTicker').value;
    if (!stockTicker) {
		alert('Ticker required');
		return;
    }
    console.log(stockTicker);
    let url = new URL(window.location.origin + `/Assignment_4/MyServlet`); 
  
    url.searchParams.append('stockTicker', stockTicker);
    
    console.log(url); 

	fetch(url)
		.then(response => {
			if(!response.ok) {
				throw new Error('Network response not ok');
			}
			return response.json();
		})
		.then(data => {
			displayStockInfo(stockTicker, data);
		})
		.catch(error => console.error('Failed to fetch data:', error));
	
}

async function displayStockInfo(stockTicker, data) {
	
	console.log(data);
	
	const quoteData = data.quote;
	const profileData = data.profile;
	
	if (quoteData.d == null) {
		alert('Invalid Ticker');
		return; 
	}
	document.getElementById('symbol').innerText = stockTicker;
	document.getElementById('company').innerText = `${profileData.name}`;
    document.getElementById('exchange').innerText = `${profileData.exchange}`;
    document.getElementById('highPrice').innerText = `High Price: ${parseFloat(quoteData.h).toFixed(2)}`;
    document.getElementById('lowPrice').innerText = `Low Price: ${parseFloat(quoteData.l).toFixed(2)}`;
    document.getElementById('openPrice').innerText = `Open Price: ${parseFloat(quoteData.o).toFixed(2)}`;
    document.getElementById('closePrice').innerText = `Close Price: ${parseFloat(quoteData.pc).toFixed(2)}`;
    document.getElementById('ipoDate').innerText = `${profileData.ipo}`;
    document.getElementById('marketCap').innerText = `${profileData.marketCapitalization}`;
    document.getElementById('shareOutstanding').innerText = `${profileData.shareOutstanding}`;
    document.getElementById('website').innerText = `${profileData.weburl}`;
    document.getElementById('phone').innerText = `${profileData.phone}`;
    
    
    const username = localStorage.getItem('user');
    if(username) {
		document.getElementById('userHigh').innerText = `${quoteData.h}`;
		document.getElementById('userChange').innerText = `${quoteData.d}`;
		document.getElementById('userPercent').innerText = `(${parseFloat(quoteData.dp).toFixed(2)}%)`;
		document.getElementById('quantityBox').style.display = 'block';
		document.getElementById('buyButton').style.display = 'block';
		
		
		document.getElementById('symbolCompanyExchange').style.textAlign = 'left';
		document.getElementById('symbolCompanyExchange').style.left = '65px';
		document.getElementById('symbolCompanyExchange').style.marginLeft = '40px';
		document.getElementById('dateTime').style.display = 'block';
		document.getElementById('highPrice').style.fontWeight = 'bold';
		document.getElementById('lowPrice').style.fontWeight = 'bold';
		document.getElementById('openPrice').style.fontWeight = 'bold';
		document.getElementById('closePrice').style.fontWeight = 'bold';
		
		
		document.getElementById('field').style.display = 'block';
		
		
		if (getMarketStatus()) {
			document.getElementById('marketStatus').innerText = 'Market is Open';
		}
		else {
			document.getElementById('marketStatus').innerText = 'Market is Closed';
		}
			
		document.getElementById('marketStatus').style.display = 'block'; 
		
		document.getElementById('quantity').innerText = 'Quantity: '; 
		document.getElementById('quantity').style.display = 'block'; 
		
		document.getElementById('summary-line').style.width = '85%'; 
		document.getElementById('info-line').style.width = '85%'; 
		
		document.getElementById('companyInfo').style.paddingLeft = '110px'; 
		
		// Adjust color
		const change = document.getElementById('userChange').innerText;
		console.log('change', change);
		
		if (change > 0) {
			let icon = '<i class="fas fa-caret-up"></i>';
			document.getElementById('userChange').innerHTML = `${icon} ${change}`;
			document.getElementById('userPrivileges').style.color = 'green';
		}
		else {
			let icon = '<i class="fas fa-caret-down"></i>';
			document.getElementById('userChange').innerHTML = `${icon} ${change}`;
			document.getElementById('userPrivileges').style.color = 'red';
		}
		
		
	} 
	
	console.log('display symbol: ', stockTicker);
  
    document.getElementById('stockInfo').style.display = 'block';
    document.getElementById('searchContainer').style.display = 'none';
    document.getElementById('searchStocksTitle').style.display = 'none';
    
}

function getMarketStatus() {
	const now = new Date();
	const hours = padZero(now.getHours());
	const minutes = padZero(now.getMinutes());
	const currTime = hours + minutes;
	
	const marketOpen = 630;
	const marketClose = 1300;
	
	console.log('time: currTime', currTime);
	
	if ( (currTime >= marketOpen) && (currTime < marketClose) ) {
		return true; 
	}
	else {
		return false;
	}
}


function updateDateTime() {
	const now = new Date();
	const day = padZero(now.getDate());
	const month = padZero(now.getMonth() + 1);
	const year = now.getFullYear();
	const hours = padZero(now.getHours());
	const minutes = padZero(now.getMinutes());
	const seconds = padZero(now.getSeconds());
	
	const formattedDate = `${day}-${month}-${year} ${hours}:${minutes}:${seconds}`;
	document.getElementById('dateTime').innerText = formattedDate;
}

function padZero(num) {
    return num < 10 ? '0' + num : num.toString();
}

function submitTrade() {
	
	/*
	if (!getMarketStatus()) {
		alert('Market is closed');
		return;
	}
	
	*/
	
	const stockTicker = document.getElementById('stockTicker').value;;
	const username = localStorage.getItem('user');
	const quantity = document.getElementById('quantityBox').value;
	const buySellChoice = 'buy';
	
	if (quantity < 1) {
		alert('Please enter a quantity > 0');
		return;
	}
	
	console.log('stockTicker: ', stockTicker);
	console.log('Quantity: ', quantity);
	console.log('buySellChoice: ', buySellChoice)
	
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
			reportTrade(stockTicker, quantity, data);
		})
		.catch(error => console.error('Failed to fetch data:', error));
}

function reportTrade(stockTicker, quantity, data) {
	
	if (!data) {
		alert('FAILED, transaction not possible');
		return;
	}
	
	alert('Bought ' + quantity + ' shares of ' + stockTicker + ' for $ ' + parseFloat(data.result).toFixed(2));
	return;
}

