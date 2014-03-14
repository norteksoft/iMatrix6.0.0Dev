package com.norteksoft.product.util.excel;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.poi.ss.usermodel.Cell;

import com.ibm.icu.text.SimpleDateFormat;

public class DateTypeCellFormatter extends CellFormatter {
    private final static String DEFAULT_FORMAT="yyyy-MM-dd";//EXCEL格式设置不区分大小写，java区分
    public DateTypeCellFormatter(Cell cell) {
        super(cell);
    }
    @Override
    protected String getDefaultFormat() {
        return DEFAULT_FORMAT;
    }

    @Override
    protected void setCellValue(Object value) {
		SimpleDateFormat dft = new SimpleDateFormat(DEFAULT_FORMAT); 
		Date cellValue;
		try {
			cellValue = dft.parse(value.toString());
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(cellValue);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			cellValue = calendar.getTime();
		} catch (ParseException e) {
			throw new RuntimeException("cell value is not a date!");
		}
		cell.setCellValue(cellValue);
    }
    
    @Override
    protected void setDefaultCellValue(Object value) {
    	cell.setCellValue(value.toString());
    }
}
