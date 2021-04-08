package eu.h2020.helios_social.core.info_control.classifier;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Set;

/**
 * ProbabilityTable
 * - two-dimensional hash table.
 * It holds integer values, and hashed cell and column names,
 * and total sums of rows and columns.
 */
public class ProbabilityTable {
    private final HashMap<String,Integer> rowTable;
    private final HashMap<String,Integer> columnTable;
    private final HashMap<String, HashMap<String, Integer>> table2D;

    public ProbabilityTable() {
        rowTable = new HashMap<>();
        columnTable = new HashMap<>();
        table2D = new HashMap<>();
    }

    public int get(@NonNull String row, @NonNull String column) {
        HashMap<String, Integer> rowMap = table2D.get(row);
        if(rowMap != null) {
            Integer cellValue = rowMap.get(column);
            if(cellValue != null) {
                return cellValue;
            }
        }
        return 0;
    }

    public void increase(@NonNull String row, @NonNull String column) {
        HashMap<String, Integer> table = table2D.get(row);
        if(table == null) {
            table = new HashMap<String,Integer>();
            table2D.put(row, table);
        }
        table.put(column, incrementValue(table.get(column)));
        rowTable.put(row, incrementValue(rowTable.get(row)));
        columnTable.put(column, incrementValue(columnTable.get(column)));
    }

    public void decrease(@NonNull String row, @NonNull String column) {
        HashMap<String, Integer> table = table2D.get(row);
        if(table != null) {
            int value = decrementValue(table.get(column));
            if(value > 0) {
                table.put(column, value);
            } else {
                table.remove(column);
            }
            int count = decrementValue(rowTable.get(row));
            if(count > 0) {
                rowTable.put(row, count);
            } else {
                table2D.remove(row);
                rowTable.remove(row);
            }
            count = decrementValue(columnTable.get(column));
            if(count > 0) {
                columnTable.put(column, count);
            } else {
                columnTable.remove(column);
            }
        }
    }

    public Set<String> getColumns() {
        return columnTable.keySet();
    }

    public Set<String> getRows() {
        return rowTable.keySet();
    }

    public int getRowSum(String row) {
        Integer sum = rowTable.get(row);
        return sum != null ? sum : 0;
    }

    public int getColumnSum(String column) {
        Integer sum = columnTable.get(column);
        return sum != null ? sum : 0;
    }

    public void reset() {
        rowTable.clear();
        columnTable.clear();
        table2D.clear();
    }

    private static int incrementValue(Integer value) {
        return value != null ? value + 1 : 1;
    }

    private static int decrementValue(Integer value) {
        return value != null && value > 0 ? value - 1 : 0;
    }

}
