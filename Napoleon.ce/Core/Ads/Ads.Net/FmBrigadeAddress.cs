using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using System.Net;
using System.IO;
using System.Xml;
using System.Globalization;

namespace GRSoft.Ads
{
   public partial class FmBrigadeAddress : Form
   {
      private DsBrigade dsBrigade;
      private BrigadeAddress address;

      public FmBrigadeAddress()
      {
         InitializeComponent();
         dsBrigade = (DsBrigade)DataModule.Get(Brigade.OBJECT_NAME) ??
            new DsBrigade(true);
         dgvAddress.AutoGenerateColumns = false;
      }

      private void FmBrigadeAddress_Load(object sender, EventArgs e)
      {
         Brigade[] brigade = new Brigade[dsBrigade.Count];
         dsBrigade.Values.CopyTo(brigade,0);

         Array.Sort(brigade, new Comparison<Brigade>(
            delegate(Brigade b1, Brigade b2){return b1.Name.CompareTo(b2.Name);}));
         cbBrigade.Items.AddRange(brigade);
         btnSave.Enabled = false;
      }

      private void tbnAdd_Click(object sender, EventArgs e)
      {
         FmKladr fmKladr = new FmKladr(new IsOK(IsOk));

         if (fmKladr.ShowDialog() == DialogResult.OK)
         {
            Brigade brigade = cbBrigade.SelectedItem as Brigade;

            if (brigade != null)
            {
               brigade.address.Add(address);
               FillGrid();
               btnSave.Enabled = true;
            }
         }
      }

      private bool IsOk(object param)
      {
         string address = (string)param;
         Location loc = GetLocation((string)param);
         
         if (loc != null)
         {
            this.address = new BrigadeAddress();
            this.address.address = address;
            this.address.latitude = loc.Latitude;
            this.address.longitude = loc.Longitude;

            string txt = MapEngine.OrgAddress(Config.GetConfig().mapSource, address, loc);
            //File.WriteAllText("ttt2.html", txt);
            wb.DocumentText = txt;

            return true;
         }
         else
         {
            MessageBox.Show("Невозможно определить координаты точки по адресу, " +
               "возможно это является причиной отказа сервиса Яндекса, "+
               "попробуйте задать запрос позже.", "Ошибка",MessageBoxButtons.OKCancel, MessageBoxIcon.Error);
            return false;
         }
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         Brigade b = cbBrigade.SelectedItem as Brigade;

         if (b != null)
         {
            string txt = MapEngine.BrigadeAddress(Config.GetConfig().mapSource, b.address);
            //File.WriteAllText("ttt2.html", txt);
            wb.DocumentText = txt;
         }
      }

      private static Dictionary<string, Location> cachedLocations = new Dictionary<string, Location>();

      public static XmlDocument GetYandexRequest(string reqStr)
      {
         HttpWebRequest request = (HttpWebRequest)WebRequest.Create(
            "http://geocode-maps.yandex.ru/1.x/?geocode=" + reqStr +
            "&key=ANzXmEoBAAAAkjQFPwIAQphddQGLBfkqE3qCI3Vo8OCM8yYAAAAAAAAAAACYpxbA0OaEdJRuAHC50onaXfYLBQ==");

#pragma warning disable 618
         request.Proxy = WebProxy.GetDefaultProxy();

         Config c = Config.GetConfig();
         if (c.proxyLogin.Length > 0)
            request.Credentials = new NetworkCredential(c.proxyLogin, c.proxyPassword);

         HttpWebResponse response = (HttpWebResponse)request.GetResponse();

         Stream resStream = response.GetResponseStream();
         int count = 0;
         StringBuilder sb = new StringBuilder();
         byte[] buf = new byte[8192];
         do
         {
            count = resStream.Read(buf, 0, buf.Length);
            if (count != 0)
               sb.Append(Encoding.UTF8.GetString(buf, 0, count));
         } while (count > 0);

         XmlDocument doc = new XmlDocument();
         doc.LoadXml(sb.ToString());

         return doc;
      }

      static public Location GetLocation(string address)
      {
         Location location = null;
         if (cachedLocations.ContainsKey(address))
         {
            return cachedLocations[address];
         }

         if (address != null && address.Length > 0)
         {
            try
            {
               XmlDocument doc = GetYandexRequest(address);

               XmlNodeList result = doc.GetElementsByTagName("featureMember");
               foreach (XmlNode node in result)
               {
                  if (GoodPrecision(node))
                  {
                     XmlNodeList posList = (node as XmlElement).GetElementsByTagName("pos");
                     if (posList.Count > 0)
                     {
                        string posText = posList.Item(0).InnerText;
                        string[] posA = posText.Split(new char[] { ' ' });
                        location = new Location();
                        CultureInfo en = CultureInfo.GetCultureInfo("en-US");
                        location.Longitude = double.Parse(posA[0], en);
                        location.Latitude = double.Parse(posA[1], en);

                        break;
                     }
                  }
               }
            }
            catch (Exception)
            {
               //using (StreamWriter w = new StreamWriter("log.txt", true))
               //{
               //   w.Write(e.Message);
               //   w.Flush();
               //}
            }
         }

         if (location != null)
            cachedLocations[address] = location;
         return location;
      }

      static bool GoodPrecision(XmlNode node)
      {
         XmlElement element = node as XmlElement;
         if (element == null) return false;

         bool res = false;
         XmlNodeList resCount = element.GetElementsByTagName("precision");
         if (resCount.Count > 0)
         {
            //XmlNode n = resCount.Item(0);
            //if (n.InnerText == "exact" || n.InnerText == "near" || n.InnerText == "street")
            res = true;
         }

         return res;
      }

      private void FillGrid()
      { 
         Brigade b = cbBrigade.SelectedItem as Brigade;
         dgvAddress.DataSource = null;

         if (b.address.Count > 0)
         {
            if (b != null)
            {
               List<BrigadeAddress> list = new List<BrigadeAddress>();
               list.AddRange(b.address);
               dgvAddress.DataSource = list;
            }
         }
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         Brigade b = cbBrigade.SelectedItem as Brigade;

         if (b != null)
         {
            List<IDataSet> list = new List<IDataSet>();
            DsBrigade dsb = new DsBrigade(false);
            list.Add(dsb);
            dsb.Add(b.id, b);

            if (!DataModule.UpdateDataSet(list, null, null,
               Config.GetConfig().GetConnection()))
               MessageBox.Show("Ошибка записи в базу данных");
            else
               btnSave.Enabled = false;
         }
      }

      private void cbBrigade_SelectedIndexChanged(object sender, EventArgs e)
      {
         FillGrid();
         btnSave.Enabled = false;
      }

      private void btnDel_Click(object sender, EventArgs e)
      {
         Brigade b = cbBrigade.SelectedItem as Brigade;

         if (b != null)
         {
            DataGridViewRow row = dgvAddress.CurrentRow;

            if (row != null)
            {
               BrigadeAddress ba = row.DataBoundItem as BrigadeAddress;

               if (ba != null)
               {
                  if (MessageBox.Show("Внимание, запись будет удалена.", 
                     "Вопрос", MessageBoxButtons.OKCancel, 
                     MessageBoxIcon.Question) == DialogResult.OK)
                  {
                     b.address.Remove(ba);
                     FillGrid();
                     btnSave.Enabled = true;
                  }
               }
            }
         }
      }

      private void dgvAddress_DoubleClick(object sender, EventArgs e)
      {
         DataGridViewRow row = dgvAddress.CurrentRow;

         if (row != null)
         {
            BrigadeAddress ba = row.DataBoundItem as BrigadeAddress;

            if (ba != null)
            {
               string address = ba.address;
               Location loc = new Location(ba.latitude, ba.longitude);
               string txt = MapEngine.OrgAddress(Config.GetConfig().mapSource, address, loc);
               //File.WriteAllText("ttt2.html", txt);
               wb.DocumentText = txt;
            }
         }
      }

      private void FmBrigadeAddress_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (DialogResult == DialogResult.OK &&
               btnSave.Enabled &&
               MessageBox.Show(this, "Сохранить изменения?", "Вопрос",
               MessageBoxButtons.OKCancel) == DialogResult.OK)
            btnSave_Click(null, null);
      }
   }
}
