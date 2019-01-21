package test.sales.gen.view;

import java.util.List;
import test.sales.gen.bean.Product;

public interface ProductListView {
  void onProductListReloaded(List<Product> productList);

  void onProductListAppended(List<Product> productList);
}