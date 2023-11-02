import { currency } from "../assets/currency.js";

export function getOfferLink(country){
  var file = 'offerge.pdf'
  if (['RUS', 'BLR'].includes(country))
    file = 'offerrus.pdf'

  return "/" + file
}

export function getCurrency(id) {
  if (id in currency) return currency[id];

  return "\u0024";
}

export function fmtDate(val)
{
  if (val)
    return `${val.substring(6,8)}.${val.substring(4,6)}.${val.substring(0,4)}`
  else
    ''
}

export function fmtTime(val)
{
  if (val)
    return `${val.substring(8,10)}:${val.substring(10,12)}:${val.substring(12,14)}`
  else
    ''
}
