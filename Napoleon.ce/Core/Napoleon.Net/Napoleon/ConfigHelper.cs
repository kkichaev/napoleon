using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Napoleon
{
    class ConfigHelper
    {
        public static readonly string SHEDULE_START = "SheduleStart";

        public static string GetValue(string name)
        {
            string result = string.Empty;

            Dictionary<string, Config> cfg = Update.GetStoredDictionary<Config>(Config.OBJECT_NAME);

            if (cfg.ContainsKey(name))
                result = cfg[name].value;

            return result;
        }
    }
}
