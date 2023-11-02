using GRSoft.Network;
using System;
using System.Collections;
using System.Collections.Generic;
using System.Reflection;
using System.Text;

namespace SyncDocs
{
   // datasets changes routines
   public partial class Form1
   {
      public bool CheckDoc(BaseDocument doc)
      {
         if (agentsChange.ContainsKey(doc.userid) == false || agentsChange[doc.userid] == "")
            return false;
         if (orgsChange.ContainsKey(doc.id) == false || orgsChange[doc.id] == "")
            return false;
         return true;
      }

      bool CheckItems(ICollection items)
      {
         foreach (ItemBase item in items)
            if (priceChange.ContainsKey(item.id) == false || priceChange[item.id] == "")
               return false;

         return true;
      }

      bool CheckAllAssigned()
      {
         foreach (VandAudit doc in dsAudit.Data)
         {
            if (!CheckDoc(doc) || !CheckItems(doc.items))
               return false;
         }

         foreach (VandSales doc in dsSales.Data)
         {
            if (!CheckDoc(doc) || !CheckItems(doc.items))
               return false;
         }

         foreach (VandReload doc in dsReload.Data)
         {
            if (!CheckDoc(doc) || !CheckItems(doc.items))
               return false;
         }

         return true;
      }

      void PutChangesData(SimpleDataSet<SyncObjects> dest, Dictionary<string, string> src, string srcType)
      {
         foreach (KeyValuePair<string, string> kv in src)
         {
            if (kv.Value == null || kv.Value.Length == 0)
               continue;

            SyncObjects so = new SyncObjects();
            so.srcId = kv.Key;
            so.destId = kv.Value;
            so.type = srcType;

            dest.Add(so);
         }
      }

      public void PrepareSet(List<ReplacedSet> rpl, List<VandAudit> docs, bool checkItems)
      {
         Dictionary<string, ReplacedSet> sets = new Dictionary<string, ReplacedSet>();
         foreach (VandAudit doc in docs)
         {
            VandAudit dest = new VandAudit();
            if (ConvertDoc(dest, doc, checkItems))
            {
               if (sets.ContainsKey(dest.userid) == false)
               {
                  SimpleDataSet<VandAudit> va = new SimpleDataSet<VandAudit>(VandAudit.OBJECT_NAME, false);
                  ReplacedSet rs = new ReplacedSet(dest.userid, va);
                  rs.dontRemove = true;
                  sets[dest.userid] = rs;
                  va.Add(dest);
               }
               else
               {
                  (sets[dest.userid].data as SimpleDataSet<VandAudit>).Add(dest);
               }
            }
         }
         foreach (ReplacedSet rs in sets.Values)
            if (rs.data.Count > 0)
               rpl.Add(rs);
      }

      public void PrepareSet(List<ReplacedSet> rpl, List<VandSales> docs, bool checkItems)
      {
         Dictionary<string, ReplacedSet> sets = new Dictionary<string, ReplacedSet>();
         foreach (VandSales doc in docs)
         {
            VandSales dest = new VandSales();
            if (ConvertDoc(dest, doc, checkItems))
            {
               if (sets.ContainsKey(dest.userid) == false)
               {
                  SimpleDataSet<VandSales> va = new SimpleDataSet<VandSales>(VandSales.OBJECT_NAME, false);
                  ReplacedSet rs = new ReplacedSet(dest.userid, va);
                  sets[dest.userid] = rs;
                  rs.dontRemove = true;
                  va.Add(dest);
               }
               else
               {
                  (sets[dest.userid].data as SimpleDataSet<VandSales>).Add(dest);
               }
            }
         }

         foreach (ReplacedSet rs in sets.Values)
            if (rs.data.Count > 0)
               rpl.Add(rs);
      }

      public void PrepareSet(List<ReplacedSet> rpl, List<VandReload> docs, bool checkItems)
      {
         Dictionary<string, ReplacedSet> sets = new Dictionary<string, ReplacedSet>();
         foreach (VandReload doc in docs)
         {
            VandReload dest = new VandReload();
            if (ConvertDoc(dest, doc, checkItems))
            {
               if (sets.ContainsKey(dest.userid) == false)
               {
                  SimpleDataSet<VandReload> va = new SimpleDataSet<VandReload>(VandReload.OBJECT_NAME, false);
                  ReplacedSet rs = new ReplacedSet(dest.userid, va);
                  sets[dest.userid] = rs;
                  rs.dontRemove = true;
                  va.Add(dest);
               }
               else
               {
                  (sets[dest.userid].data as SimpleDataSet<VandReload>).Add(dest);
               }
            }
         }

         foreach (ReplacedSet rs in sets.Values)
            if (rs.data.Count > 0)
               rpl.Add(rs);
      }

      void CopyFields(object dest, object src)
      {
         FieldInfo[] flds = src.GetType().GetFields(BindingFlags.Public | BindingFlags.Instance);
         foreach (FieldInfo f in flds)
         {
            if (f.FieldType.IsGenericType && f.FieldType.GetGenericTypeDefinition() == typeof(List<>))
               continue;

            try
            {
               object val = f.GetValue(src);
               f.SetValue(dest, val);
            }
            catch (Exception)
            {
            }
         }
      }

      bool ConvertHead(BaseDocument dest, BaseDocument src)
      {
         if (agentsChange.ContainsKey(src.userid) == false)
            return false;

         if (orgsChange.ContainsKey(src.id) == false)
            return false;

         CopyFields(dest, src);
         dest.userid = agentsChange[src.userid];
         dest.id = orgsChange[src.id];
         if (dest.userid == "" || dest.id == "" || avalonAgents.ContainsKey(dest.userid) == false || avalonOrgs.ContainsKey(dest.id) == false)
            return false;

         dest.agent = avalonAgents[dest.userid];
         dest.org = avalonOrgs[dest.id];
         return true;
      }

      bool ConvertItem(ItemBase dest, ItemBase src)
      {
         if (priceChange.ContainsKey(src.id) == false)
            return false;

         CopyFields(dest, src);
         dest.id = priceChange[src.id];
         if (dest.id == "" || avalonPrice.ContainsKey(dest.id) == false)
            return false;

         dest.item = avalonPrice[dest.id];
         return true;
      }

      private bool ConvertDoc(VandAudit dest, VandAudit doc, bool checkItems)
      {
         if (!ConvertHead(dest, doc))
            return false;

         foreach (VandAudit.Item i in doc.items)
         {
            if (i.id.Length == 0)
               continue;

            VandAudit.Item di = new VandAudit.Item();
            if (ConvertItem(di, i))
               dest.items.Add(di);
            else
            {
               if (checkItems)
                  return false;
            }
         }

         return true;
      }

      private bool ConvertDoc(VandSales dest, VandSales doc, bool checkItems)
      {
         if (!ConvertHead(dest, doc))
            return false;

         foreach (VandSales.Item i in doc.items)
         {
            if (i.id.Length == 0)
               continue;

            VandSales.Item di = new VandSales.Item();
            if (ConvertItem(di, i))
               dest.items.Add(di);
            else
            {
               if (checkItems)
                  return false;
            }
         }

         return true;
      }

      private bool ConvertDoc(VandReload dest, VandReload doc, bool checkItems)
      {
         if (!ConvertHead(dest, doc))
            return false;

         foreach (VandReload.Item i in doc.items)
         {
            if (i.id.Length == 0)
               continue;

            VandReload.Item di = new VandReload.Item();
            if (ConvertItem(di, i))
               dest.items.Add(di);
            else
            {
               if (checkItems)
                  return false;
            }
         }

         return true;
      }
   }
}
