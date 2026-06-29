import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router';

const CaseArchiveView = () => import('../views/CaseArchiveView.vue');
const CaseDetailView = () => import('../views/CaseDetailView.vue');
const InvestigationView = () => import('../views/InvestigationView.vue');
const TruthReportView = () => import('../views/TruthReportView.vue');
const ReportResultView = () => import('../views/ReportResultView.vue');

const routes: RouteRecordRaw[] = [
  { path: '/', name: 'case-archive', component: CaseArchiveView },
  { path: '/cases/:caseId', name: 'case-detail', component: CaseDetailView, props: true },
  { path: '/sessions/:sessionId', name: 'investigation', component: InvestigationView, props: true },
  { path: '/sessions/:sessionId/report', name: 'truth-report', component: TruthReportView, props: true },
  { path: '/reports/:reportId', name: 'report-result', component: ReportResultView, props: true }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;