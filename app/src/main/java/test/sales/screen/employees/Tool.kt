package test.sales.screen.employees

import test.sales.gen.FilterLogicExpr
import test.sales.gen.OrderByListExpr
import test.sales.gen.RangeExpr
import test.sales.gen.bean.Employee

internal fun toFilterLogicExpr(keyword: String, filterStatusList: List<Int>, filterGenderList: List<String>): FilterLogicExpr? {
    val filter = FilterLogicExpr()
    if (keyword.isNotEmpty()) {
        val filterKeyword = FilterLogicExpr(FilterLogicExpr.OP_OR)
        filterKeyword.containsString(Employee.FIELD_EMNAME, keyword)
        if (keyword.matches(Regex("^[-+]?[0-9]+$")))
            filterKeyword.equalsNumber(Employee.FIELD_EMID, keyword.toInt())
        filter.append(filterKeyword)
    }
    if (filterStatusList.isNotEmpty())
        filter.append(FilterLogicExpr(FilterLogicExpr.OP_OR).apply { filterStatusList.forEach { equalsNumber(Employee.FIELD_EMSTATUS, it) } })
    if (filterGenderList.isNotEmpty())
        filter.append(FilterLogicExpr(FilterLogicExpr.OP_OR).apply { filterGenderList.forEach { equalsString(Employee.FIELD_EMGENDER, it) } })
    return if (filter.data.isEmpty()) null else filter
}

internal fun toOrderByListExpr(resultSort: Pair<String, Boolean>): OrderByListExpr {
    return OrderByListExpr().append(resultSort.first, resultSort.second)
}

internal fun toRangeExpr(existingSize: Int, pageSize: Int): RangeExpr {
    return RangeExpr(existingSize.toLong(), (existingSize + pageSize - 1).toLong())
}


open class Query(
        val keyword: String,
        val filterStatusList: List<Int>,
        val filterGenderList: List<String>,
        val resultSort: Pair<String, Boolean>,
        val pageSize: Int)

open class Load(
        keyword: String,
        filterStatusList: List<Int>,
        filterGenderList: List<String>,
        resultSort: Pair<String, Boolean>,
        pageSize: Int) : Query(keyword, filterStatusList, filterGenderList, resultSort, pageSize)

open class QueryContinue(
        keyword: String,
        filterStatusList: List<Int>,
        filterGenderList: List<String>,
        resultSort: Pair<String, Boolean>,
        val from: Int,
        pageSize: Int) : Query(keyword, filterStatusList, filterGenderList, resultSort, pageSize)