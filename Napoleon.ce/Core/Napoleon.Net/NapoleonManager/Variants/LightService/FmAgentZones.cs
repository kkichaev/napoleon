using GRSoft.NapoleonManager.Utils;
using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    [System.Security.Permissions.PermissionSet(System.Security.Permissions.SecurityAction.Demand, Name = "FullTrust")]
    [System.Runtime.InteropServices.ComVisibleAttribute(true)]
    public partial class FmAgentZones : Form
    {
        public static Color[] AllColors = new Color[] { Color.Red, Color.Green, Color.Blue, Color.Orange, Color.Violet, Color.Gold, Color.Gray, Color.Yellow, Color.Black };
        int colorIndex = 0;
        bool showAll = true;

        SimpleDataSet<AgentRouteLightService> dsRoutes = new SimpleDataSet<AgentRouteLightService>(AgentRouteLightService.OBJECT_NAME, false);
        List<AgentRouteLightService> routes = new List<AgentRouteLightService>();
        public FmAgentZones()
        {
            InitializeComponent();


            webBrowser1.DocumentText = DocHtml;
            //webBrowser1.DocumentText = File.ReadAllText(@"D:\Works\Napoleon.ce\Projects\LightService\!todo\map.html");

            dgvAgents.AutoGenerateColumns = false;
            dgvPoints.AutoGenerateColumns = false;

            dtpFinish.Value = DateTime.Now.Date;
            dtpStart.Value = FirstDayOfMonth(dtpFinish.Value.AddMonths(-1));

            //AgentRouteLightService.PutData(routes);
            //AgentRouteLightService i = routes[0];
            //i.agent = Agents.GetDataSet()[i.uid];
            //i.ColorRef = ColorFromIndex(0);

            //dgvAgents.DataSource = new SortableBindingList<AgentRouteLightService>(routes);
        }

        DateTime FirstDayOfMonth(DateTime dt) { return new DateTime(dt.Year, dt.Month, 1);  }


        private void dgvAgents_DataError(object sender, DataGridViewDataErrorEventArgs e)
        {
        }

        private void dgvPoints_DataError(object sender, DataGridViewDataErrorEventArgs e)
        {
        }

        //Color ColorFromIndex(int i)
        //{
        //    return AllColors[i % AllColors.Length];
        //    //return Color.FromArgb(((i + 1) & 1) > 0 ? 255 - (i / 3 * 10) % 255 : 0, 
        //    //    ((i + 1) & 2) > 0 ? 255 - (i / 3 * 10) % 255 : 0, 
        //    //    ((i + 1) & 4) > 0 ? 255 - (i / 3 * 10) % 255: 0);
        //}

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            List<Agent> agents = new List<Agent>();
            foreach(Agent a in (CurrentUser.user as Manager).GetAgents().Data)
            {
                agents.Add(a);
            }

            FmSelectAgent fm = new FmSelectAgent();
            fm.SetAgents(agents);
            if(fm.ShowDialog() == DialogResult.OK)
            {
                List<Agent> added = fm.Checked;
                foreach(AgentRouteLightService ar in routes)
                {
                    foreach(Agent a in added)
                    {
                        if(ar.uid == a.id)
                        {
                            added.Remove(a);
                            break;
                        }
                    }
                }

                if(added.Count > 0)
                {
                    GetData(added);
                }
            }
        }

        void GetData(List<Agent> agents)
        {
            string uids = "";
            foreach (Agent a in agents)
                uids += a.id + ",";

            AgentRouteParam param = new AgentRouteParam();
            param.ids = uids.Substring(0, uids.Length - 1);
            param.start = dtpStart.Value;
            param.end = dtpFinish.Value;

            List<IDataSet> upd = new List<IDataSet>();

            upd.Add(new Report("agents_route", param, dsRoutes));
            FmWait.StdDataRefresh(this, upd, DoLoadData, null);
        }

        void DoLoadData()
        {
            foreach(AgentRouteLightService ar in dsRoutes.Data)
            {
                ar.ColorIndex = colorIndex++;
                ar.RemoveStartFinish();
                routes.Add(ar);
            }

            dgvAgents.DataSource = new SortableBindingList<AgentRouteLightService>(routes);
            fitPolygons();
            DrawOrgs();
        }

        private void btnRefresh_Click(object sender, EventArgs e)
        {
            if (routes.Count == 0)
                return;

            List<Agent> agents = new List<Agent>();
            foreach(AgentRouteLightService ar in routes)
            {
                agents.Add(ar.agent);
            }

            colorIndex = 0;
            routes.Clear();
            GetData(agents);
        }

        void fitPolygons()
        {
            List<object> bnds = new List<object>();
            routes.ForEach(x => {
                if(x.orgs.Count >0) {
                    if (x.Polygon == null)
                        x.Polygon = webBrowser1.Document.InvokeScript("addPolygon", x.PolygonParams);
                    bnds.Add(x.Polygon);
                }
            });
            webBrowser1.Document.InvokeScript("fitPolygons", bnds.ToArray());
        }

        //private void toolStripButton4_Click(object sender, EventArgs e)
        //{
        //    ////webBrowser1.DocumentText = File.ReadAllText(@"D:\Works\Napoleon.ce\Projects\LightService\!todo\map.html");

        //    //AgentRouteLightService i = routes[0];
        //    //i.Polygon = webBrowser1.Document.InvokeScript("addPolygon", i.PolygonParams);

        //    //List<object> bnds = new List<object>();
        //    //bnds.Add(i.Polygon);
        //    //object[] param = bnds.ToArray();
        //    //webBrowser1.Document.InvokeScript("fitPolygons", param);
        //}

        private void toolStripButton2_Click(object sender, EventArgs e)
        {
            List<AgentRouteLightService> rmv = new List<AgentRouteLightService>();
            foreach(DataGridViewRow r in dgvAgents.SelectedRows)
            {
                rmv.Add(r.DataBoundItem as AgentRouteLightService);
            }

            rmv.ForEach(x => {
                webBrowser1.Document.InvokeScript("removePolygon", new object[] { x.Polygon });
                ((SortableBindingList<AgentRouteLightService>)dgvAgents.DataSource).Remove(x);
            });

            if(routes.Count > 0)
                fitPolygons();
            DrawOrgs();
        }

        void DrawOrgs()
        {
            List<OrgItem> src = new List<OrgItem>();
            if(showAll)
            {
                foreach(AgentRouteLightService r in routes)
                {
                    foreach(AgentRouteLightService.Item i in r.orgs)
                    {
                        OrgItem oi = new OrgItem(i, r);
                        src.Add(oi);
                    }
                }
            } else
            {
                foreach (AgentRouteLightService r in routes)
                    for (int i = routes.IndexOf(r) + 1; i < routes.Count; i++)
                    {
                        AgentRouteLightService chk = routes[i];
                        List<AgentRouteLightService.Item> s1 = new List<AgentRouteLightService.Item>();
                        List<AgentRouteLightService.Item> s2 = new List<AgentRouteLightService.Item>();
                        r.GetIntersects(chk, s1, s2);

                        s1.ForEach(x => src.Add(new OrgItem(x, r)));
                        s2.ForEach(x => src.Add(new OrgItem(x,chk)));
                    }
            }

            dgvPoints.DataSource = new SortableBindingList<OrgItem>(src);
        }

        private void toolStripButton3_Click(object sender, EventArgs e)
        {
            showAll = !showAll;
            tsInfo.Text = showAll ? "Все точки" : "Пересечение";

            DrawOrgs();
        }

        public class OrgItem
        {
            public AgentRouteLightService.Item src;
            public AgentRouteLightService owner;

            public OrgItem(AgentRouteLightService.Item src, AgentRouteLightService owner)
            {
                this.src = src;
                this.owner = owner;
            }

            //public int Index { get; set;  }
            public string Org { get { return src.name; }  }
            public string Address { get { return src.address; } }
            public string Agent { get { return owner.agent.name; } }
            public double Income { get { return src.income; } }

        }

        private void dgvPoints_RowEnter(object sender, DataGridViewCellEventArgs e)
        {
            OrgItem oi = dgvPoints.Rows[e.RowIndex].DataBoundItem as OrgItem;

            webBrowser1.Document.InvokeScript("openMarkerAt", new object[] { oi.owner.Polygon, oi.src.lat, oi.src.lon });
            
        }

        static readonly string DocHtml = @"
<!DOCTYPE HTML>
<html lang='en'>
  <head>
    <meta charset='utf-8' />
    <meta http-equiv='X-UA-Compatible' content='IE=Edge' />
    <meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>
 <link rel='stylesheet' href='https://unpkg.com/leaflet@1.7.1/dist/leaflet.css' 
    integrity='sha512-xodZBNTC5n17Xt2atTPuE1HxjVMSvLVW9ocqUKLsCC5CXdbqCmblAshOMAS6/keqq/sMZMZ19scR4PsZChSR7A==' crossorigin=''/>
<script src='https://unpkg.com/leaflet@1.7.1/dist/leaflet.js'
   integrity='sha512-XQoYMqMTK8LvdxXYG3nZ448hOEQiglfqkJs1NOQV44cWnUrBc8PkAOcXy20w0vlaXaVUearIOBhiXZ5V3ynxwA==' crossorigin=''></script>
 <style>
      html, body {
        height: 100%;
        padding: 0;
        margin: 0;
      }
      #map {
        /* configure the size of the map */
        width: 100%;
        height: 100%;
      }
    </style>
  </head>
  <body>
    <div id='map'></div>
    <script>
      var map = L.map('map');
      var icons = [];
      ['red','green','blue','orange','violet','gold','grey','yellow','black'].forEach(function(el) {
        var icn = new L.Icon({
          iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-' + el +'.png',
          shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
          iconSize: [25, 41],
          iconAnchor: [12, 41],
          popupAnchor: [1, -34],
          shadowSize: [41, 41]
        });
        icons.push(icn);
      });

      var homeColor = '#583470';
      var markerHtmlStyles = 'background-color:'+ homeColor + ';width: 3rem;height: 3rem;display: block;left: -1.5rem;top: -1.5rem;position: relative;border-radius: 3rem 3rem 0;transform: rotate(45deg);border: 1px solid #FFFFFF';

      var homeIcon = L.divIcon({
        className: 'my-custom-pin',
        iconAnchor: [0, 24],
        labelAnchor: [-6, 0],
        popupAnchor: [0, -36],
        html: '<span style=""'+ markerHtmlStyles + '"" />'});


      function addPolygon(polyJSON, color, orgJSON, iconIndex)
        {
            var points = JSON.parse(polyJSON);
            var orgs = JSON.parse(orgJSON);

            let polygon = L.polygon(points, { color: color});
            let addHome = false;

            polygon.orgs = [];
            orgs.forEach(function(el) {
                if (el.isHome)
                {
                    if (!addHome)
                    {
                        var mrk = L.marker({ lon: el.lon, lat: el.lat}, { icon: homeIcon}).bindPopup(el.name);
                        mrk.addTo(map);
                        polygon.orgs.push(mrk);
                    }
                    addHome = true;
                }
                else
                {
                    var mrk = L.marker({ lon: el.lon, lat: el.lat}, { icon: icons[iconIndex % icons.length]}).bindPopup(el.name);
                    mrk.addTo(map);
                    polygon.orgs.push(mrk);
                }
            });

            return polygon;
        }

        function fitPolygons(plg) {
        var bnds = null;
        for(var i = 0; i < arguments.length; i++) {
          var el = arguments[i];

          if(!el) continue;

          el.remove();
          el.addTo(map);

          if(!bnds) bnds = el.getBounds();
          else bnds.extend(el.getBounds());
        }
        map.fitBounds(bnds);
      }

      function removePolygon(plg) { 
        if(plg) { 
          if(plg.orgs) {
            plg.orgs.forEach(function(x){ x.remove(); });
          }
          plg.remove(); 
        }
      }

      function openMarkerAt(plg, lat, lon) {
        if(plg && plg.orgs) {
          var ll = L.latLng(lat, lon);

          plg.orgs.forEach(function(el){
              if(el.getLatLng().equals(ll)) {
                el.openPopup();                
              }
          });
        }
      }

      // add the OpenStreetMap tiles
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19,
        attribution: '&copy; <a href=""https://openstreetmap.org/copyright"">OpenStreetMap contributors</a>'
      }).addTo(map);

    // show the scale bar on the lower left corner
    L.control.scale().addTo(map);
    </script>
  </body>
</html>
";
    }
}
