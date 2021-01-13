package application;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.exceptions.OpenException;
import com.monitorjbl.xlsx.exceptions.ReadException;
import com.monitorjbl.xlsx.sst.BufferedStringsTable;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.RETURN_BLANK_AS_NULL;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class mainWindowContr {
	private Logger log = LogManager.getLogger(mainWindowContr.class.getName());
	private config cfg;
	private long startDate=0;
	private long endDate=0;
	private long timenow = 0;
	DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	DateFormat dfnow = new SimpleDateFormat("YYYY-MM-DD");
	ArrayList<String> headers = null;
	ArrayList<String> invList = null;
	Map<Integer, ArrayList<Integer>> inverters = new TreeMap<Integer, ArrayList<Integer>>();
	ArrayList<Long> times = new ArrayList<Long>();
	private ObservableList<machine> list=FXCollections.observableArrayList();
	private ObservableList<timetable> tt=FXCollections.observableArrayList();
	private ObservableList<timetable> ttswap=FXCollections.observableArrayList();
	public mainWindowContr(config cfg) {
		this.cfg=cfg;
	}
	@FXML
	private Button confirmBtn;
	@FXML
	private DatePicker dateFrom;
	@FXML
	private DatePicker dateTo;
	@FXML
	private Button export;
	@FXML
	private TableView<machine> dataTable;
	@FXML
	private TableColumn<?, ?> idcol;
	@FXML
	private TableColumn<?, ?> namecol;
	@FXML
	private TableColumn<?, ?> volcol;
	@FXML
	private TableView<timetable> timeTables;
	@FXML
	private TableColumn<?, ?> datecol;
	@FXML
	private TableColumn<?, ?> daycol;
	@FXML
	private TableColumn<?, ?> startcol;
	@FXML
	private TableColumn<?, ?> stopcol;
	@FXML
	private TableColumn<timetable, Boolean> applycol;
	private boolean rootA;
	private boolean rootB;
	private int total=0;
	private String dateName;
	@FXML
	void initialize() {
		long timeName=Calendar.getInstance().getTimeInMillis();
		dateName = dfnow.format(timeName);
		headers=new ArrayList<String>();
		invList=new ArrayList<String>();
		headers.add("milliseconds");
		headers.add("Date");
		for (Entry<Integer, machine> entry : cfg.machine.entrySet()) { 
			
			list.add(new machine(entry.getValue().getId(),entry.getValue().getName(),entry.getValue().getVolume(),entry.getValue().getInv()));
            headers.add(entry.getValue().getName());
        		
            } 
		for (Entry<Integer, machine> entry : cfg.analog.entrySet()) { 
        	list.add(new machine(entry.getValue().getId(),entry.getValue().getName(),entry.getValue().getVolume(), entry.getValue().getInv()));
        	headers.add(entry.getValue().getName());
    		
        } 
		dateFrom.setOnAction(new EventHandler() {
			@Override
		     public void handle(Event t) {
		         LocalDate date = dateFrom.getValue();
		         Instant instant = date.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
		         startDate = instant.toEpochMilli(); 
		         
		     }

		 });
		dateTo.setOnAction(new EventHandler() {
			@Override
		     public void handle(Event t) {
		         LocalDate date = dateTo.getValue();
		         Instant instant = date.atTime(LocalTime.MIDNIGHT).atZone(ZoneId.systemDefault()).toInstant();
		         endDate = instant.toEpochMilli(); 
		         
		     }
		 });
		
		
		idcol.setCellValueFactory(new PropertyValueFactory("Id"));
		namecol.setCellValueFactory(new PropertyValueFactory("name"));
		volcol.setCellValueFactory(new PropertyValueFactory("volume"));
		dataTable.setItems(list);
		
		datecol.setCellValueFactory(new PropertyValueFactory("date"));
		daycol.setCellValueFactory(new PropertyValueFactory("day"));
		//startcol.setCellValueFactory(new PropertyValueFactory("start"));
		//stopcol.setCellValueFactory(new PropertyValueFactory("end"));
		//applycol.setCellValueFactory(new PropertyValueFactory("apply"));
		
		startcol.setCellValueFactory(c->{
			timetable candidateConfig = (timetable) c.getValue();
			TextField textField = new TextField();
			textField.setText(candidateConfig.getStart());
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
				candidateConfig.setStart(newValue);
				candidateConfig.setFullDate(candidateConfig.getDate() + " "+candidateConfig.getStart()+":00");
			});
			
			return new SimpleObjectProperty(textField);
			
		});
		stopcol.setCellValueFactory(c->{
			timetable candidateConfig = (timetable) c.getValue();
			TextField textField = new TextField();
			textField.setText(candidateConfig.getEnd());
			textField.textProperty().addListener((observable, oldValue, newValue) -> {
				candidateConfig.setEnd(newValue);
				candidateConfig.setFullEndDate(candidateConfig.getDate()+" "+candidateConfig.getEnd()+":00");
			});
			return new SimpleObjectProperty(textField);
			
		});
		
		
		 applycol.setCellValueFactory(cd -> cd.getValue().changedProperty());
		 applycol.setCellFactory(col -> new TableCell<timetable, Boolean>() {

		        final Button btn = new Button("Apply");
		        {
		            btn.setOnAction(evt -> {
		            	timetable item = (timetable) getTableRow().getItem();
		                upTo(item);
		                }
		            );
		        }

		        @Override
		        protected void updateItem(Boolean item, boolean empty) {
		        	timetable shutter = (timetable) getTableRow().getItem();
		            super.updateItem(item, empty);

		            if (empty || item == null) {
		                setGraphic(null);
		            } else {
		                btn.setDisable(item);
		                setGraphic(btn);
		                
		            }
		        }

		    });
		
		timeTables.setItems(tt);
		confirmBtn.setOnAction(e->{
			createTimetable(startDate,endDate);
		});
		export.setOnAction(e->{
			getData();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText("Upload Complete!");
			alert.setContentText("Data was succesfully extracted to the source folder!");

			alert.showAndWait();
		});
		
	}
	void upTo(timetable t) {
		
		for(timetable t1: tt) {
			if(t.getDay().equals(t1.getDay())) {
				t1.setFullDate(t1.getDate() + " "+t.getStart()+":00");
				t1.setStart(t.getStart());
				t1.setEnd(t.getEnd());
				t1.setFullEndDate(t1.getDate()+" "+t.getEnd()+":00");
			}
		}
		ttswap.addAll(tt);
		tt.clear();
		tt.addAll(ttswap);
		ttswap.clear();
		
		
	}
	
	void createTimetable(long startDate, long endDate) {
		DateFormat dif = new SimpleDateFormat("dd/M/yyyy");
		DateFormat dayformat = new SimpleDateFormat("EEEE");
		long day = 24*60*60*1000;
		for(long i = startDate;i<endDate;i+=day) {
			
			String date = dif.format(i);
			String dayName = dayformat.format(i);
			timetable t = new timetable(date,dayName);
			t.setTime(i);
			tt.add(t);
			
		}
	}
	
	@SuppressWarnings("deprecation")
	void getData() {
		long timenow = Calendar.getInstance().getTimeInMillis();
		if(endDate==0) {
			endDate=timenow;
		}
		int timeEntries =0;
		//Create blank workbook
		if (cfg.db_url != null) {
			// db_conn = setupDBConnection();
			log.debug("Loading ODBC driver...");

			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				log.debug("Driver loaded!");
			} catch (ClassNotFoundException e) {
				log.error("Errore nella connessione", "Errore durante la connessione al db", e);
							// throw new IllegalStateException("Cannot
							// find the driver in the classpath!", e);
			}
			log.debug("Connecting database...");
			Connection db_conn = null;

			try {
				db_conn = DriverManager.getConnection(cfg.db_url, cfg.db_user, cfg.db_password);
				log.debug("Database connected!");
			} catch (SQLException | NullPointerException e) {
				log.error("Errore nella connessione", "Errore durante la connessione al db", e);
			}
			String query=null;
			if(!cfg.analog.entrySet().isEmpty()) {
				query = "Select time from analog_1 where time between "+startDate+" and "+endDate;
			}else {
				query = "Select time from machine_1 where time between "+startDate+" and "+endDate;
			}
			  Workbook wb = new SXSSFWorkbook(100);
		      Sheet spreadsheet = wb.createSheet("data");
		      Row headerRow = spreadsheet.createRow(0);
		      FileOutputStream out;
		   // Create cells
		        for(int i = 0; i < headers.size(); i++) {
		            Cell cell = headerRow.createCell(i);
		            cell.setCellValue(headers.get(i));
		        }
			try {
				
				int columnCount = 0;
				int rowCount=0;
				PreparedStatement prst = db_conn.prepareStatement(query);
				ResultSet res = prst.executeQuery();
				while(res.next()) {
					timeEntries++;
					Row row = spreadsheet.createRow(++rowCount);
					Cell cell = row.createCell(columnCount);
					
					cell.setCellValue(res.getLong(1));
					Cell cell2 = row.createCell(++columnCount);
					
					cell2.setCellValue(df.format(res.getDouble(1)));
					columnCount = 0;
				}
				res.close();
				      System.out.println("Timestamps recorded succesfully");

				      try {
							out = new FileOutputStream("data.xlsx");
							wb.write(out);
							wb.close();
							out.close(); 
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

			char distinct = 'a';
			for (Map.Entry<Integer, machine> i : cfg.machine.entrySet()) {
				  FileOutputStream out1=null;
				  FileInputStream fis=null;
				  Workbook workbook1=null;
				  Sheet sheet1=null;
				  Workbook workbook2=null;
						
				  try {
					  if(i.getValue().getId()==1) {
					  fis= new FileInputStream("data.xlsx");}
					  else {
						  fis= new FileInputStream(i.getValue().getId()-1+".xlsx");
					  }
					  workbook1= StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(fis);
					  
					  workbook2 = new SXSSFWorkbook(1);
					  sheet1 = workbook2.createSheet();
					  log.debug("read file succesfully");
					  
					  } catch (Exception e1) { 
					   e1.printStackTrace(); }
				String queryMachines = "Select status from machine_"+i.getValue().getId()+" where time between "+startDate+" and "+endDate ;
				
				try {
					PreparedStatement prst = db_conn.prepareStatement(queryMachines);
					ResultSet resM = prst.executeQuery();
					int rowC=1;
					if(rowC==timeEntries) {
							break;
						}	
						
						for (Sheet sheet : workbook1){
							for (Row r1 : sheet) {
								ArrayList<Object> ao=new ArrayList<Object>();
								if(r1.getRowNum()==0) {
									for(Cell mc : r1) {
											ao.add(mc.getStringCellValue());
										
										}
								}else {
								for(Cell mc : r1) {
									
									if(mc.getColumnIndex()>1) {
										if(mc.getStringCellValue().length()>3) {
											ao.add(mc.getStringCellValue());
										}else {
									ao.add(Double.toString(mc.getNumericCellValue())+distinct);
									}
								}else if(mc.getColumnIndex()==1){
									ao.add(mc.getStringCellValue());
									
								}else {
									ao.add(mc.getNumericCellValue());
								}
									}
								if(resM.next()) {
									ao.add(resM.getLong(1));
									
									}
								}
								
						
						//int columnC = 0;
						
						Row row = sheet1.createRow(r1.getRowNum());
						rowC++;
						for(Object o : ao) {
							
							Cell cell = row.createCell(ao.indexOf(o));
							if(o instanceof Double) {
								cell.setCellValue((Double)o);}
							else if(o instanceof String){
								cell.setCellValue((String)o);
							}
							else{
								cell.setCellValue((Long)o);
							}
							}
						}
						
					}
					
					resM.close();
						
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				 try {
						out1 = new FileOutputStream(i.getValue().getId()+".xlsx");
						workbook2.write(out1);
						workbook2.close();
						workbook1.close();
						fis.close();
						out1.close(); 
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				distinct++;
				log.debug("Retrieved data from machine "+i.getValue().getId());
				 File myObj=new File(i.getValue().getId()-1+".xlsx");
				 myObj.delete();
			}
			File myObj=new File("data.xlsx");
			 myObj.delete();
		}
		
		
		//Cleaning the previously meta file, doing multiplications, generating final files
		Integer lastKey = 0;
        //you entered Map<Long, String> entry
        for (Map.Entry<Integer, machine> entry : cfg.machine.entrySet()) {
            lastKey = entry.getKey();
        }
        for(timetable t : tt) {
			  times.add(t.getTime());
			  times.add(t.getEndTime());
		  }
        
        FileOutputStream outb=null;
        FileOutputStream outc=null;
		FileInputStream fisa=null;
		Workbook workbooka=null;
		Sheet sheet1=null;
		Workbook workbookb=null;
		Sheet sheet2=null;
		Workbook workbookc=null;
		try {
			fisa= new FileInputStream(lastKey+".xlsx");}
			catch(Exception e){}
		workbooka= StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(fisa);
		workbookc = new SXSSFWorkbook(100);
		sheet2 = workbookc.createSheet();
		workbookb = new SXSSFWorkbook(100);
		sheet1 = workbookb.createSheet();
		log.debug("read file succesfully");
		for (Sheet sheet : workbooka){
			for (Row r1 : sheet) {
				Row r = sheet1.createRow(r1.getRowNum());
				Row r2 = sheet2.createRow(r1.getRowNum());
				if(r1.getRowNum()==0) {
					for(Cell mc : r1) {
						Cell cell = r.createCell(mc.getColumnIndex());
						cell.setCellValue(mc.getStringCellValue());
						Cell cell2 = r2.createCell(mc.getColumnIndex());
						cell2.setCellValue(mc.getStringCellValue());
					}
				}else {
					for(Cell mc : r1) {
						if(mc.getColumnIndex()>1) {
							int a =Character.getNumericValue(mc.getStringCellValue().charAt(0));
							total +=a;
							
						}
					}
					if(total!=0) {
						rootB=true;
					}
					total=0;
					for(Cell mc : r1) {
						if(mc.getColumnIndex()==0) {
							for(int j = 1; j<times.size();) {
								if(times.get(j)>=mc.getNumericCellValue()) {
									if(mc.getNumericCellValue()>=times.get(j-1)) {
										rootA = true;
										break;
									}
								}
								j+=2;
							}
						}
						if(rootA) {
						Cell cell = r.createCell(mc.getColumnIndex());		
							if(mc.getColumnIndex()>1) {
								int a =Character.getNumericValue(mc.getStringCellValue().charAt(0))*cfg.machine.get(mc.getColumnIndex()-1).getVolume();
								cell.setCellValue(a);
							}
							else if(mc.getColumnIndex()==1){
								cell.setCellValue(mc.getStringCellValue());	
							}else {
								cell.setCellValue(mc.getNumericCellValue());
							}
						}else if(!rootA && rootB){
							Cell cell2 = r2.createCell(mc.getColumnIndex());		
							if(mc.getColumnIndex()>1) {
								int a =Character.getNumericValue(mc.getStringCellValue().charAt(0))*cfg.machine.get(mc.getColumnIndex()-1).getVolume();
								cell2.setCellValue(a);
							}
							else if(mc.getColumnIndex()==1){
								cell2.setCellValue(mc.getStringCellValue());	
							}else {
								cell2.setCellValue(mc.getNumericCellValue());
							}
						}
						}
					rootA=false;
					rootB=false;
					}
				}
		}
		  try {
				outb = new FileOutputStream(dateName+" "+ cfg.panel+" "+ cfg.version+ ".xlsx");
				outc = new FileOutputStream(dateName+" "+ cfg.panel+" "+ cfg.version+ "overtime.xlsx");
				workbookc.write(outc);
				workbookb.write(outb);
				workbookb.close();
				workbooka.close();
				workbookc.close();
				fisa.close();
				outb.close(); 
				outc.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}

}

