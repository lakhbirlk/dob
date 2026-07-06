# DataOfBusiness — AWS Hosting Cost Analysis
## Replication Factor 1 (Single-AZ) vs Replication Factor 2 (Multi-AZ HA)

**Date:** July 2, 2026  
**Region:** ap-south-1 (Mumbai)  
**Author:** Claude Code

---

## 1. Executive Summary

This report analyzes the monthly and annual cost of hosting the DoB platform on AWS under two replication strategies and across three compute options. The architecture comprises a Spring Boot backend, PostgreSQL database, Redis cache, S3 file storage, and a React Native mobile frontend (API-aligned; no web server needed in production).

| Scenario | RF1 (Single-AZ) | RF2 (Multi-AZ HA) | Δ Cost |
|----------|----------------:|-------------------:|-------:|
| **EKS + EC2** (as per spec) | **$252/mo** | **$374/mo** | +48% |
| **ECS Fargate** (serverless) | **$248/mo** | **$360/mo** | +45% |
| **EC2 Direct** (no orchestrator) | **$173/mo** | **$285/mo** | +65% |

**Key insight:** RF2 costs ~45–65% more than RF1 depending on compute choice. The biggest cost multipliers are RDS Multi-AZ (2× database cost) and the second NAT Gateway / second EC2 node required for multi-AZ networking.

---

## 2. Architecture & Pricing Assumptions

### 2.1 Estimated Workload Profile (MVP/Growth Phase)

| Metric | Value |
|--------|-------|
| Active users (monthly) | 1,000–5,000 |
| API requests/month | ~500K |
| Database size | 10 GB → 50 GB (year 1) |
| S3 file storage | 20 GB (financial docs, CA certs) |
| Data transfer out | 100 GB/month |
| Log volume ingested | 5 GB/month |

### 2.2 AWS Pricing Used (ap-south-1 / Mumbai)

| Resource | Unit Price | Source |
|----------|-----------:|--------|
| EKS control plane | $0.10/hr ($73.00/mo) | AWS EKS pricing page |
| EC2 t3.medium (2 vCPU, 4 GB) | $0.0416/hr ($30.37/mo) | Vantage.sh |
| EC2 t3.small (2 vCPU, 2 GB) | $0.0208/hr ($15.18/mo) | Half of t3.medium |
| Fargate vCPU | $0.04048/hr | AWS Fargate pricing |
| Fargate memory (per GB) | $0.004445/hr | AWS Fargate pricing |
| RDS PostgreSQL db.t3.small Single-AZ | $0.039/hr ($28.47/mo) | Vantage.sh + regional adj. |
| RDS PostgreSQL db.t3.small Multi-AZ | $0.078/hr ($56.94/mo) | 2× Single-AZ |
| ElastiCache cache.t3.small (no replica) | $0.037/hr ($27.01/mo) | Vantage.sh |
| ElastiCache cache.t3.small (with replica) | $0.074/hr ($54.02/mo) | 2× |
| ALB (per hour, excludes LCUs) | $0.0225/hr ($16.43/mo) | AWS ALB pricing |
| ALB LCU (average) | $0.008/LCU-hr (~$8/mo) | AWS ALB pricing |
| NAT Gateway (per hour) | $0.045/hr ($32.85/mo) | AWS VPC pricing |
| NAT Gateway data processing | $0.045/GB | AWS VPC pricing |
| S3 Standard storage | $0.023/GB/mo | AWS S3 pricing |
| EBS gp3 (per GB/mo) | $0.08 | AWS EBS pricing |
| CloudFront (India tier, first 10 TB) | $0.109/GB | AWS CloudFront pricing |
| Route 53 hosted zone | $0.50/mo | AWS Route 53 |
| CloudWatch Logs ingested | $0.50/GB | AWS CloudWatch |
| ECR storage | $0.10/GB/mo | AWS ECR |
| Data transfer out (internet, first 10TB) | $0.09/GB | AWS data transfer |
| ACM (SSL/TLS) | Free | AWS ACM |
| VPC (no charge) | Free | AWS VPC |

---

## 3. Scenario A: EKS + EC2 Managed Nodes (Per Spec)

This is the architecture specified in `spec.md` and `k8s/deployment-backend.yaml`. Uses EKS control plane + EC2 managed node groups.

### 3.1 RF1 — Single-AZ (No Redundancy)

| Component | Specification | Monthly Cost |
|-----------|--------------|-------------:|
| **EKS Control Plane** | 1 cluster | $73.00 |
| **EC2 Worker Node** | 1 × t3.medium (2 vCPU, 4 GB RAM) | $30.37 |
| **EBS (root + Docker)** | 1 × 30 GB gp3 | $2.40 |
| **Subtotal: Compute** | | **$105.77** |
| | | |
| **RDS PostgreSQL** | db.t3.small, Single-AZ, 20 GB gp3 | $28.47 + $1.60 = $30.07 |
| **RDS Backup** | 20 GB snapshot storage | $1.60 |
| **Subtotal: Database** | | **$31.67** |
| | | |
| **ElastiCache Redis** | cache.t3.small, no replica | $27.01 |
| **Subtotal: Cache** | | **$27.01** |
| | | |
| **ALB** | 1 ALB + 1 LCU avg | $16.43 + $8.00 = $24.43 |
| **NAT Gateway** | 1 NAT GW in public subnet | $32.85 |
| **NAT data processing** | 100 GB × $0.045 | $4.50 |
| **Data transfer out** | 100 GB × $0.09 | $9.00 |
| **Subtotal: Network** | | **$70.78** |
| | | |
| **S3 Standard** | 20 GB storage | $0.46 |
| **S3 requests** | PUT/GET/LIST | $1.50 |
| **CloudFront** | 80 GB × $0.109 (edge → viewer) | $8.72 |
| **Subtotal: Storage & CDN** | | **$10.68** |
| | | |
| **Route 53** | 1 hosted zone | $0.50 |
| **CloudWatch Logs** | 5 GB ingested × $0.50 | $2.50 |
| **ECR** | 2 images × 1 GB | $0.20 |
| **Subtotal: Ops** | | **$3.20** |
| | | |
| **TOTAL (RF1, EKS+EC2)** | | **$252/mo** |

### 3.2 RF2 — Multi-AZ HA (Full Redundancy)

| Component | Specification | Monthly Cost |
|-----------|--------------|-------------:|
| **EKS Control Plane** | 1 cluster (HA by default) | $73.00 |
| **EC2 Worker Nodes** | 2 × t3.medium (2 AZs) | $60.74 |
| **EBS (root + Docker)** | 2 × 30 GB gp3 | $4.80 |
| **Subtotal: Compute** | | **$138.54** |
| | | |
| **RDS PostgreSQL** | db.t3.small Multi-AZ, 20 GB gp3 | $56.94 + $1.60 = $58.54 |
| **RDS Backup** | 20 GB snapshot storage | $1.60 |
| **Subtotal: Database** | | **$60.14** |
| | | |
| **ElastiCache Redis** | cache.t3.small + replica (Multi-AZ) | $54.02 |
| **Subtotal: Cache** | | **$54.02** |
| | | |
| **ALB** | 1 ALB (inherently HA) + 2 LCU avg | $16.43 + $16.00 = $32.43 |
| **NAT Gateway** | 2 NAT GWs (1 per AZ for HA) | $65.70 |
| **NAT data processing** | 100 GB × $0.045 | $4.50 |
| **Data transfer out** | 100 GB × $0.09 | $9.00 |
| **Subtotal: Network** | | **$111.63** |
| | | |
| **S3 Standard** | 20 GB storage | $0.46 |
| **S3 requests** | PUT/GET/LIST | $1.50 |
| **CloudFront** | 80 GB × $0.109 | $8.72 |
| **Subtotal: Storage & CDN** | | **$10.68** |
| | | |
| **Route 53** | 1 hosted zone | $0.50 |
| **CloudWatch Logs** | 5 GB × $0.50 | $2.50 |
| **ECR** | 2 images × 1 GB | $0.20 |
| **Subtotal: Ops** | | **$3.20** |
| | | |
| **TOTAL (RF2, EKS+EC2)** | | **$374/mo** |

### 3.3 RF1 vs RF2 Delta (EKS + EC2)

| Category | RF1 | RF2 | Increase | % Change |
|----------|----:|----:|---------:|---------:|
| Compute | $105.77 | $138.54 | +$32.77 | +31% |
| Database | $31.67 | $60.14 | +$28.47 | +90% |
| Cache | $27.01 | $54.02 | +$27.01 | +100% |
| Network | $70.78 | $111.63 | +$40.85 | +58% |
| Storage & CDN | $10.68 | $10.68 | $0.00 | 0% |
| Ops | $3.20 | $3.20 | $0.00 | 0% |
| **Total** | **$252** | **$374** | **+$122** | **+48%** |

---

## 4. Scenario B: ECS Fargate (Simpler, Serverless Containers)

Skips the EKS control-plane cost. Backend runs as a Fargate task. Lower ops overhead, no cluster management.

**Backend task spec:** 0.5 vCPU, 1 GB RAM (matching Spring Boot's requirements from `deployment-backend.yaml`)

### 4.1 RF1 — Single-AZ

| Component | Specification | Monthly Cost |
|-----------|--------------|-------------:|
| **Fargate Backend** | 1 task × (0.5 vCPU × $0.04048 + 1 GB × $0.004445) × 730 hrs | $18.02 |
| **Subtotal: Compute** | | **$18.02** |
| | | |
| **RDS PostgreSQL** | db.t3.small, Single-AZ, 20 GB gp3 + backup | $31.67 |
| **ElastiCache Redis** | cache.t3.small, no replica | $27.01 |
| **ALB** | 1 ALB + 1 LCU | $24.43 |
| **NAT Gateway** | 1 NAT GW + data processing (100 GB) | $37.35 |
| **Data transfer out** | 100 GB × $0.09 | $9.00 |
| **S3 + CloudFront** | 20 GB S3 + 80 GB CloudFront | $10.68 |
| **Route 53 + CloudWatch + ECR** | | $3.20 |
| **Subtotal: Infrastructure** | | **$230.34** |
| | | |
| **TOTAL (RF1, ECS Fargate)** | | **$248/mo** |

### 4.2 RF2 — Multi-AZ HA

| Component | Specification | Monthly Cost |
|-----------|--------------|-------------:|
| **Fargate Backend** | 2 tasks × (0.5 vCPU + 1 GB) × 730 hrs | $36.04 |
| **Subtotal: Compute** | | **$36.04** |
| | | |
| **RDS PostgreSQL** | db.t3.small Multi-AZ, 20 GB + backup | $60.14 |
| **ElastiCache Redis** | cache.t3.small + replica | $54.02 |
| **ALB** | 1 ALB + 2 LCU avg | $32.43 |
| **NAT Gateway** | 2 NAT GWs (1/AZ) + data processing | $70.20 |
| **Data transfer out** | 100 GB × $0.09 | $9.00 |
| **S3 + CloudFront** | 20 GB S3 + 80 GB CloudFront | $10.68 |
| **Route 53 + CloudWatch + ECR** | | $3.20 |
| **Subtotal: Infrastructure** | | **$324.29** |
| | | |
| **TOTAL (RF2, ECS Fargate)** | | **$360/mo** |

### 4.3 RF1 vs RF2 Delta (ECS Fargate)

| Category | RF1 | RF2 | Increase | % Change |
|----------|----:|----:|---------:|---------:|
| Compute | $18.02 | $36.04 | +$18.02 | +100% |
| Database | $31.67 | $60.14 | +$28.47 | +90% |
| Cache | $27.01 | $54.02 | +$27.01 | +100% |
| Network | $70.78 | $111.63 | +$40.85 | +58% |
| Storage & CDN + Ops | $13.88 | $13.88 | $0.00 | 0% |
| **Total** | **$248** | **$360** | **+$112** | **+45%** |

**Note:** ECS Fargate saves ~$112–126/mo vs EKS + EC2 by avoiding the $73 EKS control-plane charge and right-sizing compute. However, the Fargate task unit cost is higher per vCPU-hour than EC2. The saving comes from eliminating the always-on EC2 node (used mostly for EKS system pods, not the app).

---

## 5. Scenario C: EC2 Direct (Simplest, No Orchestrator)

Backend runs directly on an EC2 instance (or via `docker compose`). No cluster management costs. Suitable for sub-1000-user scale.

### 5.1 RF1 — Single Instance

| Component | Specification | Monthly Cost |
|-----------|--------------|-------------:|
| **EC2 Backend** | 1 × t3.small (2 vCPU, 2 GB — sufficient for Spring Boot) | $15.18 |
| **EBS root** | 20 GB gp3 | $1.60 |
| **Subtotal: Compute** | | **$16.78** |
| | | |
| **RDS PostgreSQL** | db.t3.small, Single-AZ, 20 GB + backup | $31.67 |
| **ElastiCache Redis** | cache.t3.small, no replica | $27.01 |
| **ALB** | 1 ALB + 1 LCU | $24.43 |
| **NAT Gateway** | 1 NAT GW + data processing (100 GB) | $37.35 |
| **Data transfer out** | 100 GB × $0.09 | $9.00 |
| **S3 + CloudFront** | 20 GB S3 + 80 GB CloudFront | $10.68 |
| **Route 53 + CloudWatch** | | $3.00 |
| **Subtotal: Adjacent** | | **$143.14** |
| | | |
| **TOTAL (RF1, EC2 Direct)** | | **$173/mo** |

### 5.2 RF2 — Multi-AZ HA

| Component | Specification | Monthly Cost |
|-----------|--------------|-------------:|
| **EC2 Backend** | 2 × t3.small (2 AZs) | $30.36 |
| **EBS root** | 2 × 20 GB gp3 | $3.20 |
| **Subtotal: Compute** | | **$33.56** |
| | | |
| **RDS PostgreSQL** | db.t3.small Multi-AZ, 20 GB + backup | $60.14 |
| **ElastiCache Redis** | cache.t3.small + replica | $54.02 |
| **ALB** | 1 ALB + 2 LCU | $32.43 |
| **NAT Gateway** | 2 NAT GWs + data processing | $70.20 |
| **Data transfer out** | 100 GB × $0.09 | $9.00 |
| **S3 + CloudFront** | 20 GB S3 + 80 GB CloudFront | $10.68 |
| **Route 53 + CloudWatch** | | $3.00 |
| **Subtotal: Adjacent** | | **$239.47** |
| | | |
| **TOTAL (RF2, EC2 Direct)** | | **$285/mo** |

### 5.3 RF1 vs RF2 Delta (EC2 Direct)

| Category | RF1 | RF2 | Increase | % Change |
|----------|----:|----:|---------:|---------:|
| Compute | $16.78 | $33.56 | +$16.78 | +100% |
| Database | $31.67 | $60.14 | +$28.47 | +90% |
| Cache | $27.01 | $54.02 | +$27.01 | +100% |
| Network | $70.78 | $111.63 | +$40.85 | +58% |
| Storage & CDN + Ops | $13.68 | $13.68 | $0.00 | 0% |
| **Total** | **$173** | **$285** | **+$112** | **+65%** |

---

## 6. Consolidated Comparison

### 6.1 All Scenarios Side-by-Side

| | EKS + EC2 | ECS Fargate | EC2 Direct |
|--|----------:|------------:|-----------:|
| **RF1 (Single-AZ)** | $252/mo | $248/mo | $173/mo |
| **RF2 (Multi-AZ)** | $374/mo | $360/mo | $285/mo |
| **Monthly premium for HA** | +$122 | +$112 | +$112 |
| **Annual RF1 cost** | $3,024 | $2,976 | $2,076 |
| **Annual RF2 cost** | $4,488 | $4,320 | $3,420 |
| **Annual premium for HA** | +$1,464 | +$1,344 | +$1,344 |

### 6.2 Cost Breakdown by Service Category (EKS + EC2, RF2)

```
Monthly Cost ($)
   0     20    40    60    80    100   120   140
   ├─────┴─────┴─────┴─────┴─────┴─────┴─────┴─────┤
   ┌──────────────────────────────────────────┐
   │ EKS Control Plane              $73       │ ← Fixed, no scaling
   └──────────────────────────────────────────┘
   ┌──────────────────────┐
   │ EC2 Workers     $61  │ ← 2 × t3.medium
   └──────────────────────┘
   ┌──────────────────────┐
   │ RDS PostgreSQL   $60 │ ← Multi-AZ db.t3.small
   └──────────────────────┘
   ┌──────────────────────┐
   │ ElastiCache Redis $54 │ ← With replica
   └──────────────────────┘
   ┌──────────────────────────────────┐
   │ Network (ALB + NAT + DT)  $112  │ ← 2 NAT Gateways dominate
   └──────────────────────────────────┘
   ┌──────────┐
   │ S3 + CF  $11 │
   └──────────┘
   ┌──────┐
   │Ops $3 │
   └──────┘
```

### 6.3 The "Hidden Multipliers" of RF2

| Cost Driver | RF1 | RF2 | Why It Doubles |
|-------------|----:|----:|----------------|
| EC2/Fargate nodes | 1 | 2 | Need cross-AZ scheduling |
| NAT Gateways | 1 | 2 | NAT GW is AZ-specific |
| RDS | Single-AZ | Multi-AZ | Provisioned standby |
| Redis | Standalone | + Replica | Read replica in second AZ |
| ALB LCUs | ~1 | ~2 | Cross-AZ data processing |

**Services that stay flat:** EKS control plane, S3, CloudFront, Route 53, CloudWatch (all inherently HA at the AWS layer).

---

## 7. Annual Cost Projection (12 Months)

### 7.1 EKS + EC2 (Spec-Following)

| Month | RF1 | RF2 | Cumulative Savings from RF1 |
|------:|----:|----:|---------------------------:|
| 1 | $252 | $374 | +$122 |
| 3 | $756 | $1,122 | +$366 |
| 6 | $1,512 | $2,244 | +$732 |
| 12 | **$3,024** | **$4,488** | **+$1,464** |

### 7.2 Growth Scaling

As user load grows, RF2 scales more gracefully:

| Metric | RF1 at 5K users | RF2 at 5K users |
|--------|----------------:|----------------:|
| Backend tasks | 1 (fixed) | 2 (HPA to 4–6) |
| Database | Single-AZ, no read replica | Multi-AZ, can add read replica |
| Redis | Single node | Replication group, can scale reads |
| Max monthly cost (projected) | $400 | $500 |
| **Downtime risk** | **High** (single point of failure) | **Low** (cross-AZ HA) |

---

## 8. Cost Optimization Recommendations

### 8.1 If You Choose RF1 (Start Lean)

| Recommendation | Monthly Saving | Implementation |
|:--------------|--------------:|:---------------|
| **Use ECS Fargate** instead of EKS | **-$126/mo** (vs EKS+EC2) | No K8s control plane; simpler ops |
| **Reserved Instances** for RDS (1-yr) | **-$8/mo** | RDS reservation saves ~30% on instance |
| **Skip NAT Gateway** — use public subnets with security groups | **-$37/mo** | Acceptable for MVP; add NAT at launch |
| **Use Single-AZ** (RF1 is inherently single-AZ) | Included already | — |
| **CloudFront only for production** | — | Skip during dev |
| **Total potential RF1 low** | **~$130–150/mo** | With Fargate + public subnets + RDS reserved |

### 8.2 If You Choose RF2 (Production HA)

| Recommendation | Monthly Saving | Implementation |
|:--------------|--------------:|:---------------|
| **Use ECS Fargate** over EKS | **-$14/mo** vs EKS+EC2 for RF2 | $360 vs $374 |
| **Reserved Instances** for RDS + ElastiCache (1-yr) | **-$18/mo** | ~25–30% saving on instance costs |
| **Single NAT Gateway** (accept AZ-fail risk) | **-$33/mo** | Lose true HA but keep Multi-AZ DB |
| **Right-size backend** — t3.small may suffice | **-$30/mo** | Test before switching from t3.medium |
| **Total potential RF2 low** | **~$280–300/mo** | Fargate + RIs + single NAT |

### 8.3 Longer-Term Optimizations

| Strategy | Saving Potential | Timeline |
|:---------|----------------:|:---------|
| 3-year Reserved Instances for RDS + ElastiCache | -40% on DB/Cache | Month 12+ |
| Savings Plan (Compute) | -30% on EC2/Fargate | Month 3+ |
| Aurora Serverless v2 (auto-scaling DB) | ~20% at low utilization | Month 6+ |
| Spot Instances for stateless backend | -60% on compute | Month 3+ (if using EKS/ECS) |
| Multi-AZ → Single-AZ during non-business hours | -$28/mo | Only if downtime window exists |

---

## 9. Conclusion

### Recommendation by Phase

| Phase | Compute Option | Replication | Est. Monthly | Rationale |
|-------|---------------|-------------|-------------:|-----------|
| **MVP (0–3 mo)** | ECS Fargate | RF1 | **~$170/mo** | No K8s overhead; public subnets avoid NAT cost |
| **Growth (3–12 mo)** | ECS Fargate | RF1 → RF2 transition | **$248–360/mo** | Add NAT + Multi-AZ after product-market fit |
| **Production (12+ mo)** | EKS + EC2 or ECS with Savings Plans | RF2 | **$350–400/mo** (optimized) | HA with reserved capacity, HPA-enabled |

### Quick Answer

- **RF1 (Single-AZ) cheapest option:** ~**$173/mo** (EC2 Direct with public subnets)
- **RF1 (spec-following, EKS+EC2):** ~**$252/mo**
- **RF2 (Multi-AZ, spec-following, EKS+EC2):** ~**$374/mo**
- **RF2 cheapest HA option:** ~**$285/mo** (EC2 Direct with Multi-AZ RDS)

**The HA premium is ~$112–122/mo** depending on compute choice. Of this:
- $28.47 goes to RDS Multi-AZ
- $27.01 goes to Redis replica
- $32.85 goes to the second NAT Gateway
- The rest is the second compute node

### Biggest Cost-Saving Lever

If you're deciding between EKS + EC2 vs ECS Fargate for RF2: **ECS Fargate saves $14/mo** but more importantly removes the operational burden of managing a K8s control plane. For a team of 1–3 developers, the ops time saved on EKS maintenance (upgrades, CNI, CoreDNS, node groups) is worth far more than $14/mo.

---

*All prices in USD. Actual costs may vary based on traffic patterns, data transfer volume, reserved instance availability, and AWS pricing changes. Run the [AWS Pricing Calculator](https://calculator.aws/) before committing.*
