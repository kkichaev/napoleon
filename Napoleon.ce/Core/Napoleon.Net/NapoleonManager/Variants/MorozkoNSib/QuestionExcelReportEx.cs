using System;
using System.Collections.Generic;
using System.Text;

namespace GRSoft.NapoleonManager
{
   class QuestionExcelReportEx : QuestionExelReport
   {
      protected override void SetOrgData(int row, Org org, string created, string agent, double lat, double lon)
      {
         PotenzialOrg po = org as PotenzialOrg;

         if (po != null && po.region != null)
         {
            Region1 r1 = po.region.r1;
            Region2 r2 = po.region.r2;

            if (r1 != null && r2 != null)
            {
               SetValue(row, 1, r2.Name);
               SetValue(row, 2, r1.Name);
            }

            if (po.region != null)
               SetValue(row, 3, po.region.Name);
         }

         SetValue(row, 7, org.Address);
         SetValue(row, 6, org.Name);
         SetValue(row, 4, created);
         SetValue(row, 5, agent);
#if STD_QUESTION_REPORT
#else
      SetValue(row, 8, org.id);

      SetValue(row, 9, lat);
      SetValue(row, 10, lon);
      SetValue(row, 11, new Location(lat, lon).GetAddress());
#endif
      }

      protected override void Header()
      {
         SetValue(1, 1, "Область");
         SetValue(1, 2, "Район");
         SetValue(1, 3, "НП");
         SetValue(1, 7, "Адрес");
         SetValue(1, 6, "Наименование");
         SetValue(1, 4, "Дата");
#if STD_QUESTION_REPORT
         SetValue(1, 5, "Торговый представитель");
#else
         SetValue(1, 7, "Аудитор");
         SetValue(1, 8, "№РТТ");
         SetValue(1, 9, "Широта");
         SetValue(1, 10, "Долгота");
         SetValue(1, 11, "Адрес");
         SetValue(1, 12, "Категория");
         SetValue(1, 13, "Производитель");
#endif
      }
   }
}
