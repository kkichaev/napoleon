/*
 * Copyright (C), 2011, Гильдия разработчиков
 *
 * Отчет по заявкам
 * 
 * kki   19/03/2011   creating
 */


using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;

using OrgPriceT = System.Collections.Generic.KeyValuePair<GRSoft.NapoleonManager.Org, GRSoft.NapoleonManager.Reports.PricesStruct>;
using PriceT = System.Collections.Generic.KeyValuePair<GRSoft.NapoleonManager.Price, GRSoft.NapoleonManager.Reports.DataOrgPrice>;
using GroupedPriceT = System.Collections.Generic.KeyValuePair<string, GRSoft.NapoleonManager.Reports.DataPrice>;

namespace GRSoft.NapoleonManager.Reports
{
   public class PricesStruct
   {
      int valuesAmount = 0;
      private double[] values = null;

      public PricesStruct()
      {
         int finalValuesAmount = 2;
#if REPORT_INCLUDE_DELIVERIES
         finalValuesAmount += 2;
#endif
#if PoultryNSib
         finalValuesAmount += 1;
#endif
#if COVER_IN_ORDER_REPORT
         finalValuesAmount += 4;
#endif
         valuesAmount = finalValuesAmount;
         values = new double[valuesAmount];
      }

      public double GetValue(int index)
      {
         double result = 0.0;

         if (index < valuesAmount)
         {
            result = values[index];
         }

         return result;
      }

      public void SetValue(int index, double value)
      {
         if (index < valuesAmount)
         {
            values[index] = value;
         }
      }

      public int Size { get { return valuesAmount; } }
   }

   public class DataOrgPrice : Dictionary<Org, PricesStruct> { }
   public class DataPrice : Dictionary<Price, DataOrgPrice> { }
   public class DataGroupedPrice : Dictionary<string, DataPrice> { }


   class OrderReportOptions
   {
      public DataPrice data;
      public DataSet<string, ManagerFolder> folders;
      public ItemType itemType;
#if REPORT_INCLUDE_DELIVERIES
      public bool includeDeliveries;
#endif
      public DivisionItem division;
      public Agent agent;
      public DateTime begin;
      public DateTime end;
      public bool onlyTotal;
      
      public string filter;
   };

   class OrderReport : IReport
   {
      private ReportData reportData;
      private IReportImplementation reportImplementation;

      public OrderReport(OrderReportOptions options, IReportImplementation reportImplementation)
      {
         reportData = new OrderReportData(options);
         this.reportImplementation = reportImplementation;
      }

      #region IReport Members

      public void Show()
      {
         reportImplementation.Show();
      }

      public void Build()
      {
         reportImplementation.Build(reportData);
      }

      #endregion
   }

   class OrderReportData : ReportData
   {
      public OrderReportOptions options;
      public List<Org> orgs = new List<Org>();

      public OrderReportData(OrderReportOptions options)
      {
         this.options = options;

         MakeOrgsList();
      }

      public DataGroupedPrice CollectPriceRows()
      {
         DataGroupedPrice result = new DataGroupedPrice();
         foreach (PriceT row in options.data)
         {
            if (result.ContainsKey(row.Key.fid))
               result[row.Key.fid].Add(row.Key, row.Value);
            else
            {
               DataPrice dic = new DataPrice();

               dic.Add(row.Key, row.Value);
               result.Add(row.Key.fid, dic);
            }
         }

         return result;
      }

      public int GetOrgCol(Org org)
      {
         if (orgs.Contains(org))
            return orgs.IndexOf(org);

         orgs.Add(org);
         return orgs.Count;
      }

      private void MakeOrgsList()
      {
         orgs.Clear();

         foreach (DataOrgPrice dataValues in options.data.Values)
            foreach (OrgPriceT orgRow in dataValues)
               GetOrgCol(orgRow.Key);
      }
   }
}
