using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;
using System.ComponentModel;

namespace GRSoft.NapoleonManager
{
   public class ActionType : DataObject
   {
      public static readonly string OBJECT_NAME = "ActionType";

      [KeyField]
      public string id = string.Empty;
      public string name = string.Empty;

      [ItemType(typeof(QuestionItem))]
      public List<QuestionItem> items = null;

      public override string ToString()
      {
         return name;
      }
   }

   public class Action : DataObject
   {
      public static readonly string OBJECT_NAME = "Action";
      [KeyField]
      public string id = string.Empty;
      public DateTime start = DateTime.MinValue;
      public DateTime finish = DateTime.MinValue;
      public string type = string.Empty;

      public string name = "";
      public string description = "";

      [Reference("ActionType", "type", typeof(ActionType))]
      public ActionType actionType = null;

      [ItemType(typeof(ActionItem))]
      public List<ActionItem> items = null;

      public override string ToString()
      {
         StringBuilder result = new StringBuilder();
         if (actionType != null)
         {
            result.Append(name);
            result.Append(" [" + actionType.name + "]");
         }
         else
            result.Append("Новая акция");

         result.Append(" (").Append(start.ToString("dd.MM.yyyy"))
            .Append(" - ").Append(finish.ToString("dd.MM.yyyy"))
            .Append(")");

         return result.ToString();
      }
   }

   public class ActionItem : DataObject
   {
      public string id = string.Empty;

      [Reference("ManagerPrice", "id", typeof(Price))]
      public Price price = null;
   }
}
