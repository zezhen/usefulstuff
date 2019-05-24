set hive.execution.engine=tez;
set hive.cli.print.current.db=true;
set hive.cli.print.header=true;

set mapreduce.job.acl-view-job=*;

set mapred.job.queue.name=default;
use gemini_search_insights;

select
    date, 

    ad_listing_type, match_type_desc, cb_bucket_id, page_position,

    device_type, -- PC, Phone, Tablet

    ad_listing_filter,  -- tr, dd, ''

    mmb_display_url_top_domain, regexp_replace(src_tag, '_curve_.*', '') as src_tag,

    cb_canon_user_query, variant_canon_user_query, raw_bidded_term, bidded_variant_canon_term, 

    ad_id, ad_grp_id, bidded_term_id, ad_title, ad_description, display_url,

    COUNT(distinct event_guid) AS searches,
    SUM(click) AS clicks,
    SUM(clicks_advertiser_cost_usd) AS revenue,
    SUM(click) / COUNT(distinct event_guid) AS ctr,
    SUM(clicks_advertiser_cost_usd) / SUM(click) AS ppc,
    SUM(clicks_advertiser_cost_usd) / COUNT(distinct event_guid) AS rps,
    AVG(CASE WHEN ad_listing_type = 'bing' then imps_advertiser_cost_usd else imps_adv_bid_usd end) AS bid,
    AVG(mmb_normalized_clkb) AS norm_clkb,
    AVG(click_probability) AS clkb,
    AVG(mmb_line_count) as line_count,
    AVG(size(split(bidded_variant_canon_term, ' '))) as bid_term_length,
    AVG(CASE WHEN ad_listing_type = 'bing' then imps_advertiser_cost_usd * mmb_normalized_clkb else  imps_adv_bid_usd * mmb_normalized_clkb end) AS ecpm,
    SUM(mmb_variant_clkb_x_ref_ctr) / SUM(click) AS place_clkb_bias,

    COUNT(distinct CASE WHEN  page_position = 'n1' THEN event_guid else NULL end) AS n1_searches,
    SUM(CASE WHEN  page_position = 'n1' THEN click else NULL end) AS n1_clicks,
    SUM(CASE WHEN  page_position = 'n1' THEN clicks_advertiser_cost_usd else NULL end) AS n1_revenue,
    SUM(CASE WHEN  page_position = 'n1' THEN click else NULL end) / COUNT(distinct CASE WHEN  page_position = 'n1' THEN event_guid else NULL end) AS n1_ctr,
    SUM(CASE WHEN  page_position = 'n1' THEN clicks_advertiser_cost_usd else NULL end) / SUM(CASE WHEN  page_position = 'n1' THEN click else NULL end) AS n1_ppc,
    SUM(CASE WHEN  page_position = 'n1' THEN clicks_advertiser_cost_usd else NULL end) / COUNT(distinct CASE WHEN  page_position = 'n1' THEN event_guid else NULL end) AS n1_rps,
    AVG(CASE WHEN  page_position = 'n1' THEN CASE WHEN ad_listing_type = 'bing' then imps_advertiser_cost_usd else imps_adv_bid_usd end else NULL end) AS n1_bid,
    AVG(CASE WHEN  page_position = 'n1' THEN mmb_normalized_clkb else NULL end) AS n1_norm_clkb,
    AVG(CASE WHEN  page_position = 'n1' THEN click_probability else NULL end) AS n1_clkb,
    AVG(CASE WHEN  page_position = 'n1' THEN mmb_line_count else NULL end) as n1_line_count,
    AVG(CASE WHEN  page_position = 'n1' THEN size(split(bidded_variant_canon_term, ' ')) else NULL end) as n1_bid_term_length,
    AVG(CASE WHEN  page_position = 'n1' THEN CASE WHEN ad_listing_type = 'bing' then imps_advertiser_cost_usd * mmb_normalized_clkb else  imps_adv_bid_usd * mmb_normalized_clkb end else NULL end) AS n1_ecpm,
    SUM(CASE WHEN  page_position = 'n1' THEN mmb_variant_clkb_x_ref_ctr else NULL end) / SUM(CASE WHEN  page_position = 'n1' THEN click else NULL end) AS n1_place_clkb_bias,

    COUNT(distinct CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN event_guid else NULL end) AS bucket_searches,
    SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN click else NULL end) AS bucket_clicks,
    SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN clicks_advertiser_cost_usd else NULL end) AS bucket_revenue,
    SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN click else NULL end) / COUNT(distinct CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN event_guid else NULL end) AS bucket_ctr,
    SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN clicks_advertiser_cost_usd else NULL end) / SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN click else NULL end) AS bucket_ppc,
    SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN clicks_advertiser_cost_usd else NULL end) / COUNT(distinct CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN event_guid else NULL end) AS bucket_rps,
    AVG(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN CASE WHEN ad_listing_type = 'bing' then imps_advertiser_cost_usd else imps_adv_bid_usd end else NULL end) AS bucket_bid,
    AVG(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN mmb_normalized_clkb else NULL end) AS bucket_norm_clkb,
    AVG(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN click_probability else NULL end) AS bucket_clkb,
    AVG(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN mmb_line_count else NULL end) as bucket_line_count,
    AVG(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN size(split(bidded_variant_canon_term, ' ')) else NULL end) as bucket_bid_term_length,
    AVG(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN CASE WHEN ad_listing_type = 'bing' then imps_advertiser_cost_usd * mmb_normalized_clkb else  imps_adv_bid_usd * mmb_normalized_clkb end else NULL end) AS bucket_ecpm,
    SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN mmb_variant_clkb_x_ref_ctr else NULL end) / SUM(CASE WHEN cb_bucket_id = '${hiveconf:BUCKET}' THEN click else NULL end) AS bucket_place_clkb_bias

from auctions_trdd_v4

where
    date >= '${hiveconf:START_DATE}' and date <= '${hiveconf:END_DATE}'
    and (mmb_variant_clkb_x_ref_ctr >= 0 and mmb_variant_clkb_x_ref_ctr <= 1)

group by  date, ad_listing_type, match_type_desc, cb_bucket_id, page_position, device_type, ad_listing_filter, mmb_display_url_top_domain, regexp_replace(src_tag, '_curve_.*', ''), cb_canon_user_query, variant_canon_user_query, raw_bidded_term, bidded_variant_canon_term, ad_id, ad_grp_id, bidded_term_id, ad_title, ad_description, display_url

LIMIT 1000
