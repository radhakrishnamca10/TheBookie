package model.listini;

public class ItemListinoCamera {
	private Integer id;
	private Integer numGuests;
	private Double[] prices = new Double[7]; 
	
	//prezzo lunedi = prices[0]
	//prezzo martedi = prices[1]
	//prezzo mercoledi = prices[2]
	//prezzo giovedi = prices[3]
	//prezzo venerdi = prices[4]
	//prezzo sabato = prices[5]
	//prezzo domenica = prices[6]
	
	
	public Double getPrice(Integer dayOfWeek){
		//dayOfWeek
		//1 domenica
		//2 lunedi
		//3 martedi
		//4 mercoledi
		//5 giovedi
		//6 venerdi
		//7 sabato
		Double ret = 0.0;
		
		if(dayOfWeek.equals(1)){
			return prices[6];
		}
		if(dayOfWeek.equals(2)){
			return prices[0];
		}
		if(dayOfWeek.equals(3)){
			return prices[1];
		}
		if(dayOfWeek.equals(4)){
			return prices[2];
		}
		if(dayOfWeek.equals(5)){
			return prices[3];
		}
		if(dayOfWeek.equals(6)){
			return prices[4];
		}
		if(dayOfWeek.equals(7)){
			return prices[5];
		}
		return ret;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getNumGuests() {
		return numGuests;
	}
	public void setNumGuests(Integer numGuests) {
		this.numGuests = numGuests;
	}
	public Double[] getPrices() {
		return prices;
	}
	public void setPrices(Double[] prices) {
		this.prices = prices;
	}
		

}
