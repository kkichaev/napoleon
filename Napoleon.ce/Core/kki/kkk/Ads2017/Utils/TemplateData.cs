using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Ads2017
{
   public class TemplateData
   {
      public ObservableCollection<TemplateAddress> address = new ObservableCollection<TemplateAddress>();
      public string id = string.Empty;
      public string name = string.Empty;

      public string ID { get { return id; } set { id = value; } }
      public string Name { get { return name; } set { name = value; } }
      public ObservableCollection<TemplateAddress> Address { get { return address; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class TemplateAddress
   {
      public ObservableCollection<TemplateContact> contact = new ObservableCollection<TemplateContact>();
      public string name = string.Empty;

      public string Name { get { return name; } set { name = value; } }
      public ObservableCollection<TemplateContact> Contact { get { return contact; } }

      public override string ToString()
      {
         return Name;
      }
   }

   public class TemplateContact
   {
      public string name = string.Empty;
      public string phone = string.Empty;

      public string Name { get { return name; } set { name = value; } }
      public string Phone { get { return phone; } set { phone = value; } }

      public override string ToString()
      {
         return string.Format("Имя: {0}, телефон: {1}", Name, Phone);
      }
   }
}
