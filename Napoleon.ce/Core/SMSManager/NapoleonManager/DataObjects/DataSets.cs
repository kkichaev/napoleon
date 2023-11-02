using System;
using System.Collections.Generic;
using System.Text;
using GRSoft.Network;

namespace GRSoft.NapoleonManager.DataObjects
{
   class DataSets
   {
   }

   class DsLocality : DataSetFactory<DsLocality, Int32, Locality>
   {
      public DsLocality(){}
      public DsLocality(bool addToDataModule) : base(addToDataModule) { }
      public DsLocality(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }

   class DsSchoolEntity : DataSetFactory<DsSchoolEntity, Int32, SchoolEntity>
   {
      public DsSchoolEntity() { }
      public DsSchoolEntity(bool addToDataModule) : base(addToDataModule) { }
      public DsSchoolEntity(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }

   class DsStudent : DataSetFactory<DsStudent, Int32, Student>
   {
      public DsStudent() { }
      public DsStudent(bool addToDataModule) : base(addToDataModule) { }
      public DsStudent(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }

   class DsParent : DataSetFactory<DsParent, Int32, Parent>
   {
      public DsParent() { }
      public DsParent(bool addToDataModule) : base(addToDataModule) { }
      public DsParent(ActivatorAdapter aa) : base(aa.addToDataModule) { } 
   }

   class DsDogovor : DataSetFactory<DsDogovor, Int32, Dogovor>
   {
      public DsDogovor() { }
      public DsDogovor(bool addToDataModule) : base(addToDataModule) { }
      public DsDogovor(ActivatorAdapter aa) : base (aa.addToDataModule) { }
   }

   class DsSchoolSubject : DataSetFactory<DsSchoolSubject, Int32, SchoolSubject>
   {
      public DsSchoolSubject() { }
      public DsSchoolSubject(bool addToDataModule) : base(addToDataModule) { }
      public DsSchoolSubject(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }

   class DsSchedule : DataSetFactory<DsSchedule, Int32, Schedule>
   { 
      public DsSchedule() { }
      public DsSchedule(bool addToDataModule) : base(addToDataModule) { }
      public DsSchedule(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }

   class DsAgent : DataSetFactory<DsAgent, string, Agent>
   {
      public DsAgent() { }
      public DsAgent(bool addToDataModule) : base(addToDataModule) { }
      public DsAgent(ActivatorAdapter aa) : base(aa.addToDataModule) { }

      public bool GetAgentByID(string id, out Agent rAgent)
      {
         foreach (Agent agent in this.Data)
         {
            if (agent.id == id)
            {
               rAgent = agent;
               return true;
            }
         }

         rAgent = null;
         return false;
      }
   }

   class DsSchoolFolder : DataSetFactory<DsSchoolFolder, Int32, SchoolFolder>
   {
      public DsSchoolFolder() { }
      public DsSchoolFolder(bool addToDataModule) : base(addToDataModule) { }
      public DsSchoolFolder(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }

   class DsLesson : DataSetFactory<DsLesson, int, Lesson>
   { 
      public DsLesson() { }
      public DsLesson(bool addToDataModule) : base(addToDataModule) { }
      public DsLesson(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }

   class DsAnnonce : DataSetFactory<DsAnnonce, int, Annonce>
   { 
      public DsAnnonce() { }
      public DsAnnonce(bool addToDataModule) : base(addToDataModule) { }
      public DsAnnonce(ActivatorAdapter aa) : base(aa.addToDataModule) { }
   }
}
