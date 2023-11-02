package com.ashberrysoft.leadertask.xml_handlers.single;

import java.util.HashMap;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.xml_handlers.BaseSingleLionEntityHandlerNew;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SingleTaskHandler extends BaseSingleLionEntityHandlerNew<LTask> {

    public SingleTaskHandler(XMLReader reader, DefaultHandler defaultHandler) {
        super(reader, defaultHandler);
    }

    @Override
    protected LTask getEntityConstructor() {
        return new LTask();
    }

    @Override
    protected HashMap<String, Boolean> getEntityHashMap() {
        final HashMap<String, Boolean> map = new HashMap<String, Boolean>(74, 1.0f);

        map.put(LTaskContract.Uid, false);
        map.put(LTaskContract.UIDParent , false);
        map.put(LTaskContract.Order , false);
        map.put(LTaskContract.OrderNew, false);
        map.put(LTaskContract.Collapsed , false);
        map.put(LTaskContract.Name , false);
        map.put(LTaskContract.Comment , false);
        map.put(LTaskContract.Status , false);
        map.put(LTaskContract.TermBegin , false);
        map.put(LTaskContract.TermEnd , false);
        map.put(LTaskContract.EmailPerformer , false);
        map.put(LTaskContract.UidProject , false);
        map.put(LTaskContract.UidMarker , false);
        map.put(LTaskContract.Readed , false);
        map.put(LTaskContract.PerformerReaded , false);
        map.put(LTaskContract.OrderCustomer , false);
        map.put(LTaskContract.TermBeginCustomer , false);
        map.put(LTaskContract.TermEndCustomer, false);
        map.put(LTaskContract.EmailCustomer , false);
        map.put(LTaskContract.Categories , false);
        map.put(LTaskContract.Contacts , false);
        map.put(LTaskContract.CreateTime , false);
        map.put(LTaskContract.PerformTime , false);
        map.put(LTaskContract.CompleteTime , false);
        map.put(LTaskContract.SeriesType , false);
        map.put(LTaskContract.SeriesAfterType , false);
        map.put(LTaskContract.SeriesAfterCount , false);
        map.put(LTaskContract.SeriesWeekCount , false);
        map.put(LTaskContract.SeriesWeekMon , false);
        map.put(LTaskContract.SeriesWeekTue , false);
        map.put(LTaskContract.SeriesWeekWed , false);
        map.put(LTaskContract.SeriesWeekThu , false);
        map.put(LTaskContract.SeriesWeekFri , false);
        map.put(LTaskContract.SeriesWeekSat , false);
        map.put(LTaskContract.SeriesWeekSun , false);
        map.put(LTaskContract.SeriesMonthType, false);
        map.put(LTaskContract.SeriesMonthCount , false);
        map.put(LTaskContract.SeriesMonthDay , false);
        map.put(LTaskContract.SeriesMonthWeekType , false);
        map.put(LTaskContract.SeriesMonthDayOfWeek , false);
        map.put(LTaskContract.SeriesYearType, false);
        map.put(LTaskContract.SeriesYearMonth , false);
        map.put(LTaskContract.SeriesYearMonthDay , false);
        map.put(LTaskContract.SeriesYearWeekType , false);
        map.put(LTaskContract.SeriesYearDayOfWeek , false);
        map.put(LTaskContract.SeriesEnd , false);
        map.put(LTaskContract.UsnEntity , false);
        map.put(LTaskContract.UsnFieldUidParent , false);
        map.put(LTaskContract.UsnFieldEmailPerformer , false);
        map.put(LTaskContract.UsnFieldName , false);
        map.put(LTaskContract.UsnFieldComment , false);
        map.put(LTaskContract.UsnFieldStatus , false);
        map.put(LTaskContract.UsnFieldOrder , false);
        map.put(LTaskContract.UsnFieldUidProject , false);
        map.put(LTaskContract.UsnFieldUidMarker , false);
        map.put(LTaskContract.UsnFieldTerm , false);
        map.put(LTaskContract.UsnFieldReaded , false);
        map.put(LTaskContract.UsnFieldCollapsed , false);
        map.put(LTaskContract.UsnFieldCustomerOrder , false);
        map.put(LTaskContract.UsnFieldCustomerTerm , false);
        map.put(LTaskContract.UsnFieldCategories , false);
        map.put(LTaskContract.UsnFieldContacts , false);
        map.put(LTaskContract.UsnFieldCreatetime , false);
        map.put(LTaskContract.UsnFieldPerformtime , false);
        map.put(LTaskContract.UsnFieldCompletetime , false);
        map.put(LTaskContract.UsnFieldSeries , false);
        map.put(LTaskContract.UsnOrderNew , false);
        map.put(LTaskContract.UsnPerformerReaded  , false);

        map.put(LTaskContract.InWorkTime  , false);
        map.put(LTaskContract.Time  , false);
        map.put(LTaskContract.Plan  , false);
        map.put(LTaskContract.UsnPlan  , false);
        map.put(LTaskContract.UsnInWorkTime  , false);
        map.put(LTaskContract.UsnTime  , false);
        map.put(LTaskContract.UsnFieldFocus, false);
        map.put(LTaskContract.Focus, false);

        map.put(LTaskContract.UsnFieldListMembers, false);
        map.put(LTaskContract.Emails, false);

        return map;
    }
}