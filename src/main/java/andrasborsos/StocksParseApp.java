package andrasborsos;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;

public class StocksParseApp {

    private static final String done = "done";
    private static int count=0;
    private static ArrayList<StockData> stocksData=new ArrayList<>();
    private static ArrayList<String> tickers;
    private static String filePath=System.getProperty("user.dir")+"\\TradingData.xlsx";

    public static void main(String[] args) throws IOException {
        System.out.println("Type a stock's ticker and hit ENTER to look it up. Type \"" +done+"\" after the last ticker");
        tickers= readUserInput();
        preConnection(tickers);
        dataToExcel(stocksData);

    }

    private static ArrayList<String> readUserInput() {
        String ticker;
        int i = 0;
        Scanner in = new Scanner(System.in);
        ArrayList<String> constructTickers = new ArrayList<>();

        while (true) {
            System.out.print("Please enter a ticker with ALL CAPS");
            ticker = in.nextLine();
            if (ticker.equals(done)) break;
            constructTickers.add(ticker);
            i++;
        }
        return constructTickers;
    }
    public static void preConnection(ArrayList<String> symbols) {

        String link;
        int length=symbols.size();
        if (length==0) return;

        for(int i=0;i<length;i++) {
            link= "https://query2.finance.yahoo.com/v7/finance/quote?symbols=" + symbols.get(i);
            connection(link);
        }

    }
    public static void connection(String link) {
        //https://stackoverflow.com/questions/14024625/how-to-get-httpclient-returning-status-code-and-response-body

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(StocksParseApp::parsing)
                .join();    //this closes sendAsync
        count++;
    }
    public static String parsing(String responseBody){
        String name="";
        double priceNow=-1.00;
        double changePercent=0.00;
        double fiftyDayAvg=-1.00;
        double volumeTenDays=-1.00;
        double volumePreviousDay=-1.00;

        JSONObject obj=new JSONObject(responseBody);
        JSONObject quoteResponse=obj.getJSONObject("quoteResponse");
        JSONArray result=quoteResponse.getJSONArray("result");

        for(int i=0; i<result.length(); i++)
        {
            JSONObject dataField= result.getJSONObject(i);
            if((dataField.has("regularMarketPrice")))
            {
                name= dataField.getString("shortName");
                priceNow=dataField.getDouble("regularMarketPrice");
                changePercent=dataField.getDouble("regularMarketChangePercent");
                fiftyDayAvg=dataField.getDouble("fiftyDayAverage");
                volumeTenDays=dataField.getDouble("averageDailyVolume10Day");
                volumePreviousDay=dataField.getDouble("regularMarketVolume");
            }
        }

        StockData currentStock=new StockData();
        currentStock.setTicker(tickers.get(count));
        currentStock.setName(name);
        currentStock.setPriceNow(priceNow);
        currentStock.setChangePercent(changePercent);
        currentStock.setFiftyDayAvg(fiftyDayAvg);
        currentStock.setVolumeTenDays(volumeTenDays);
        currentStock.setVolumePreviousDay(volumePreviousDay);

        stocksData.add(currentStock);

        return null;
    }
    public static void dataToExcel(ArrayList<StockData> stocksData) throws IOException {
        Workbook data = new XSSFWorkbook();
        Sheet sheet = data.createSheet();
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Ticker");
        cell = row.createCell(1);
        cell.setCellValue("Name");
        cell = row.createCell(2);
        cell.setCellValue("Current price");
        cell = row.createCell(3);
        cell.setCellValue("Daily change percent");
        cell = row.createCell(4);
        cell.setCellValue("Previous day trading volume");
        cell = row.createCell(5);
        cell.setCellValue("10 day AVG trading volume");
        cell = row.createCell(6);
        cell.setCellValue("50 day AVG price");

        for(int i=0;i< stocksData.size();i++){
            row = sheet.createRow(i+1);
            cell = row.createCell(0);
            cell.setCellValue(stocksData.get(i).getTicker());
            cell = row.createCell(1);
            cell.setCellValue(stocksData.get(i).getName());
            cell = row.createCell(2);
            cell.setCellValue(stocksData.get(i).getPriceNow());
            cell = row.createCell(3);
            cell.setCellValue(stocksData.get(i).getChangePercent());
            cell = row.createCell(4);
            cell.setCellValue(stocksData.get(i).getVolumePreviousDay());
            cell = row.createCell(5);
            cell.setCellValue(stocksData.get(i).getVolumeTenDays());
            cell = row.createCell(6);
            cell.setCellValue(stocksData.get(i).getFiftyDayAvg());
        }

        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            data.write(outputStream);
        }

    }
}
