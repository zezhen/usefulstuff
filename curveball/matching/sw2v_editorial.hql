set hive.execution.engine=tez;
set hive.cli.print.current.db=true;
set hive.cli.print.header=true;

set mapreduce.job.acl-view-job=*;

set mapred.job.queue.name=default;

select
  cb_canon_user_query,
  ad_id,
  ad_description,
  ad_title,
  display_url,
  ad_grp_id,
  bidded_term_id,
  raw_bidded_term,

  COUNT(DISTINCT event_guid) as searches,
  SUM( click ) as clicks,
  sum(clicks_advertiser_cost_usd) as revenue,
  avg(mmb_normalized_clkb) as avg_clkb,
  ROUND( SUM( click ) / COUNT(distinct event_guid), 2) as ctr,
  sum(clicks_advertiser_cost_usd) / sum(click) as ppc,
  sum(clicks_advertiser_cost_usd) / count(distinct event_guid) as rps

from gemini_search_insights.auctions_trdd_v4
where
  date >= '${START_DATE}' and date <= '${END_DATE}' and
  page_placement = 'north' and
  match_type_desc = 'subword2vec' and
  cb_bucket_id = 'B4742'
group by cb_canon_user_query, ad_id, ad_description, ad_title, display_url, ad_grp_id, bidded_term_id, raw_bidded_term