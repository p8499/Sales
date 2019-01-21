package test.sales.gen.view;

import java.util.List;
import test.sales.gen.bean.Record;

public interface RecordListView {
  void onRecordListReloaded(List<Record> recordList);

  void onRecordListAppended(List<Record> recordList);
}