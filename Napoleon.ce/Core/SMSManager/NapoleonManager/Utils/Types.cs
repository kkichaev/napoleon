/*
 * Copyright (C), 2010, Гильдия Разработчиков
 *
 * Вспомогательные классы 
 * 
 * kki   28/12/2010   creating
 */
using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.NapoleonManager.DataObjects;

namespace GRSoft.NapoleonManager.Utils
{
   class Types
   {
   }

   public delegate void InvokeDelegate();

   interface Captionable
   {
      string Caption { get; }
   }

   //Общий класс для всех элементов что будут  
   //содержимым comboBox
   abstract class ListItem<ObjectType>
   {
      private ObjectType storedObject;

      public ListItem(ObjectType storedObject)
      {
         this.storedObject = storedObject;
      }

      public override string ToString()
      {
         return Caption;
      }

      public ObjectType Object { get { return storedObject; } }
      public abstract string Caption{get;}
   }

   class AgentItem : ListItem<Agent>, Captionable
   {
      public AgentItem(Agent agent):base(agent)
      {
      }

      public override string Caption
      {
         get { return Object.Name; }
      }

      public override bool Equals(object obj)
      {
         if (obj is AgentItem)
            return Object.id.Equals(((AgentItem)obj).Object.id);
         else 
            return false;
      }

      public override int GetHashCode()
      {
         return base.GetHashCode();
      }
   }
}
