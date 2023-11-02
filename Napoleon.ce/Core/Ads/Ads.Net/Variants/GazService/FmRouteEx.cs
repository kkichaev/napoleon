using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.Globalization;
using GRSoft.Ads.Utils;
using System.Windows.Forms;

namespace GRSoft.Ads
{
   class FmRouteEx : FmRoute
   {
      DsPause dsPause;
      string idBrigade;
      public string address;

      internal FmRouteEx(string idBrigade, DateTime date)
         : base(idBrigade, date)
      {
         dsPause = (DsPause)DataModule.Get(Pause.OBJECT_NAME) ?? new DsPause(true);
         this.idBrigade = idBrigade;
         Load += new EventHandler(FmRouteEx_Load);
      }

      void FmRouteEx_Load(object sender, EventArgs e)
      {
         if (idBrigade != null)
            btnRefresh.PerformClick();
      }

      protected override void AdjustFilterForDS(string idAgent, DateTime date)
      {
         base.AdjustFilterForDS(idAgent, date);
         dsPause.Filter = String.Format("pause >= ToDate('{0}') and pause < ToDate('{1}')",
            date, date.AddDays(1)); ;
      }

      protected override List<IDataSet> DataSetList()
      {
         List<IDataSet> result = base.DataSetList();
         result.Add(dsPause);
         return result;
      }

      protected override List<VisitQueueItem> MakeVisitQueue(List<Location> route)
      {
         List<VisitQueueItem> result = base.MakeVisitQueue(route);

         foreach (Pause p in dsPause.Data)
         {
            result.Add(new VisitQueueItem(p.pause, new Client(), p.plat, p.plong, new VisitTypePause()));

            if (p.resume.Year > 2000)
               result.Add(new VisitQueueItem(p.resume, new Client(), p.rlat, p.rlong, new VisitTypeResume()));
         }

         int index = 1;

         foreach (VisitQueueItem vqi in result)
            vqi.VisitNumber = index++;

         return result;
      }

      public override string CreateHTML(List<Location> route, List<VisitQueueItem> visitQueue, List<RoadPoint> roadPoint)
      {
         if (address != null && address.Length > 0)
         {
            Location loc = FmRoute.GetLocation(address);
            if (loc != null)
            {
               BrigadeAddress dist = new BrigadeAddress();
               dist.address = address;
               dist.latitude = loc.Latitude;
               dist.longitude = loc.Longitude;

               return MapEngine.RouteDistination(Config.GetConfig().mapSource, route,
                  visitQueue, roadPoint, dist);
            }
            else
               return "Ошибка в получении координат по адресу";
         }
         else
            return base.CreateHTML(route, visitQueue, roadPoint);
      }
   }

   class VisitTypePause : VisitType
   {
      public VisitTypePause()
         : base(ObjType.TObjType.OtOrder)
      {
      }

      public override string ToString()
      {
         return "Пауза";
      }
   }

   class VisitTypeResume : VisitType
   {
      public VisitTypeResume()
         : base(ObjType.TObjType.OtOrder)
      {
      }

      public override string ToString()
      {
         return "В работе";
      }
   }

   class Pause : GRSoft.Network.DataObject
   {
      static public readonly string OBJECT_NAME = "Pause";

      [Reference("Agents", "userid")]
      public Brigade agent = null;

      public DateTime pause = DateTime.MinValue;
      public double plat = 0;
      public double plong = 0;
      public DateTime resume = DateTime.MinValue;
      public double rlat = 0;
      public double rlong = 0;
   }

   internal class DsPause :
      DataSet<int, Pause>
   {
      public DsPause(bool add)
         : base(Pause.OBJECT_NAME, add)
      {
         
      }
   }
}
