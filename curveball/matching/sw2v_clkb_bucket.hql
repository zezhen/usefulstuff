set hive.execution.engine=tez;
set hive.cli.print.current.db=true;
set hive.cli.print.header=true;

set mapreduce.job.acl-view-job=*;

set mapred.job.queue.name=default;
use gemini_search_insights;

select
    cb_bucket_id,
    ROUND(click_probability, 2) as clkb_bucket,

    COUNT(DISTINCT event_guid) as searches,
    SUM( click ) as clicks,
    sum(clicks_advertiser_cost_usd) as revenue,
    --avg(mmb_normalized_clkb) as avg_clkb,
    avg(mmb_variant_clkb_x_ref_ctr) as avg_clkb,
    ROUND( SUM( click ) / COUNT(distinct event_guid), 2) as ctr,
    sum(clicks_advertiser_cost_usd) / sum(click) as ppc,
    sum(clicks_advertiser_cost_usd) / count(distinct event_guid) as rps,
    SUM(mmb_variant_clkb_x_ref_ctr) / SUM(click) AS place_clkb_bias,

    COUNT(DISTINCT CASE WHEN match_type_desc = 'subword2vec' THEN event_guid ELSE NULL END) as sw2v_searches,
    SUM(CASE WHEN match_type_desc = 'subword2vec' THEN click ELSE 0 END) as sw2v_clicks,
    sum(CASE WHEN match_type_desc = 'subword2vec' THEN clicks_advertiser_cost_usd ELSE 0 END ) as sw2v_revenue,
    avg(CASE WHEN match_type_desc = 'subword2vec' THEN mmb_normalized_clkb ELSE NULL END) as sw2v_avg_clkb,
    ROUND( SUM( CASE WHEN match_type_desc = 'subword2vec' THEN click ELSE 0 END) / COUNT(distinct CASE WHEN match_type_desc = 'subword2vec' THEN event_guid ELSE NULL END), 2) as sw2v_ctr,
    sum(CASE WHEN match_type_desc = 'subword2vec' THEN clicks_advertiser_cost_usd ELSE 0 END) / sum(CASE WHEN match_type_desc = 'subword2vec' THEN click ELSE 0 END) as sw2v_ppc,
    sum(CASE WHEN match_type_desc = 'subword2vec' THEN clicks_advertiser_cost_usd ELSE 0 END) / count(distinct CASE WHEN match_type_desc = 'subword2vec' THEN event_guid ELSE NULL END) as sw2v_rps,
    SUM(CASE WHEN match_type_desc = 'subword2vec' THEN mmb_variant_clkb_x_ref_ctr ELSE 0 END) / SUM(CASE WHEN match_type_desc = 'subword2vec' THEN click ELSE 0 END) AS sw2v_place_clkb_bias

from auctions_trdd_v4
where
    date >= '${START_DATE}' and date <= '${END_DATE}'
    and (cb_bucket_id = '${TEST_BUCKET}' or cb_bucket_id = '${CONTROL_BUCKET}')
    and page_position = 'n1'
group by cb_bucket_id, ROUND(click_probability, 2)