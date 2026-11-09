import os
from reportlab.lib.pagesizes import letter
from reportlab.lib import colors
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle

def build_pdf():
    pdf_path = "stage2-report.pdf"
    doc = SimpleDocTemplate(
        pdf_path,
        pagesize=letter,
        rightMargin=36,
        leftMargin=36,
        topMargin=36,
        bottomMargin=36
    )

    styles = getSampleStyleSheet()
    
    title_style = ParagraphStyle(
        'DocTitle',
        parent=styles['Heading1'],
        fontName='Helvetica-Bold',
        fontSize=18,
        leading=22,
        textColor=colors.HexColor('#1A2B4C'),
        alignment=1,
        spaceAfter=10
    )
    
    h1_style = ParagraphStyle(
        'SectionH1',
        parent=styles['Heading2'],
        fontName='Helvetica-Bold',
        fontSize=12,
        leading=15,
        textColor=colors.HexColor('#1A2B4C'),
        spaceBefore=12,
        spaceAfter=5
    )
    
    body_style = ParagraphStyle(
        'BodyDark',
        parent=styles['BodyText'],
        fontName='Helvetica',
        fontSize=8.5,
        leading=11.5,
        textColor=colors.HexColor('#2C3E50'),
        spaceAfter=5
    )
    
    table_cell = ParagraphStyle(
        'TableCell',
        parent=styles['BodyText'],
        fontName='Helvetica',
        fontSize=8,
        leading=10,
        textColor=colors.HexColor('#2C3E50')
    )
    
    table_cell_bold = ParagraphStyle(
        'TableCellBold',
        parent=styles['BodyText'],
        fontName='Helvetica-Bold',
        fontSize=8,
        leading=10,
        textColor=colors.HexColor('#1A2B4C')
    )

    story = []

    # Title
    story.append(Paragraph("EECS 3311 - Course Project Stage 2 Report", title_style))
    story.append(Paragraph("<b>Project:</b> Personal Finance AI Agent &nbsp;|&nbsp; <b>Author:</b> thickKar1 &nbsp;|&nbsp; <b>Date:</b> November 2026", ParagraphStyle('Sub', alignment=1, fontSize=8.5, textColor=colors.HexColor('#555555'), spaceAfter=12)))

    # Section 1: Overview
    story.append(Paragraph("1. Project Overview and Implementation Status", h1_style))
    overview_text = (
        "The Personal Finance AI Agent is an intelligent software assistant designed to handle budget tracking, "
        "transaction management, anomaly detection, bill monitoring, debt tracking, tax estimation, and multi-currency conversions. "
        "The architecture is driven by a 3-step ReAct (Reason + Act + Synthesize) loop in <code>FinanceAgent.java</code> and <code>LLMClient.java</code>. "
        "Rather than serving as a basic prompt wrapper, the AI evaluates user input, selects the appropriate localized tool, "
        "retrieves context from storage, and generates natural language responses. Both GUI (Swing) and CLI interfaces are fully functional."
    )
    story.append(Paragraph(overview_text, body_style))

    # Section 2: Feature Mapping
    story.append(Paragraph("2. Feature-to-Implementation Mapping", h1_style))
    
    headers = [Paragraph("Feature ID & Name", table_cell_bold), Paragraph("Stage 1 Ref", table_cell_bold), Paragraph("Status", table_cell_bold), Paragraph("Implemented Classes", table_cell_bold), Paragraph("Key Methods", table_cell_bold), Paragraph("Design Patterns", table_cell_bold)]
    
    mapping_data = [headers]
    features_info = [
        ("F01: Budget Tracking", "F01", "Implemented", "BudgetTool.java, BudgetTracker.java", "execute(), checkBudgetLimit()", "Strategy, Observer"),
        ("F02: Transaction Entry", "F02", "Implemented", "TransactionTool.java, DataManager.java", "execute(), saveTransaction()", "Strategy, Singleton"),
        ("F03: Financial Summary", "F03", "Implemented", "SummaryTool.java, DataManager.java", "execute(), loadData()", "Strategy, Singleton"),
        ("F04: Anomaly Detection", "F04", "Implemented", "AnomalyTool.java", "execute(), analyzeSpending()", "Strategy"),
        ("F05: Bill Management", "F05", "Implemented", "BillTool.java", "execute(), getUpcomingBills()", "Strategy"),
        ("F06: Currency Conversion", "F06", "Implemented", "CurrencyTool.java", "execute(), convert()", "Strategy"),
        ("F07: Debt Tracking", "F07", "Implemented", "DebtTool.java", "execute(), calculateDebt()", "Strategy"),
        ("F08: Savings Goals", "F08", "Implemented", "GoalTool.java", "execute(), getGoalProgress()", "Strategy"),
        ("F09: Investment Monitor", "F09", "Implemented", "InvestmentTool.java", "execute(), getPortfolio()", "Strategy"),
        ("F10: Tax Estimation", "F10", "Implemented", "TaxTool.java", "execute(), estimateTax()", "Strategy")
    ]
    
    for f in features_info:
        mapping_data.append([
            Paragraph(f[0], table_cell),
            Paragraph(f[1], table_cell),
            Paragraph(f[2], table_cell),
            Paragraph(f[3], table_cell),
            Paragraph(f[4], table_cell),
            Paragraph(f[5], table_cell)
        ])

    t_map = Table(mapping_data, colWidths=[95, 50, 60, 145, 100, 90])
    t_map.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#EAECEE')),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#BDC3C7')),
        ('VALIGN', (0,0), (-1,-1), 'TOP'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('TOPPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t_map)

    # Section 3: Design Patterns
    story.append(Paragraph("3. Implemented Design Patterns", h1_style))
    patterns_text = (
        "<b>1. Strategy Pattern:</b> Interfaced via <code>FinanceTool.java</code> across all 10 tools.<br/>"
        "<b>2. Factory Pattern:</b> Implemented in <code>ToolFactory.java</code> to centralize tool instantiation (addresses Stage 1 feedback).<br/>"
        "<b>3. Facade Pattern:</b> Implemented in <code>ApplicationFacade.java</code> providing unified CLI/GUI entry points.<br/>"
        "<b>4. Singleton Pattern:</b> Implemented in <code>DataManager.java</code> managing JSON state access.<br/>"
        "<b>5. Observer Pattern:</b> Implemented via <code>BudgetTracker.java</code>, <code>BudgetObserver.java</code>, and <code>AlertNotifier.java</code>."
    )
    story.append(Paragraph(patterns_text, body_style))

    # Section 4: Changes and Rationale
    story.append(Paragraph("4. Changes and Rationale", h1_style))
    changes_text = (
        "<b>Original Design -> Implemented Change -> Reason:</b><br/>"
        "• <i>Simple Wrapper -> ReAct Loop:</i> Updated <code>FinanceAgent</code> to analyze tools before execution to fulfill multi-step reasoning requirement.<br/>"
        "• <i>Generic Factory -> Explicit ToolFactory:</i> Standardized naming in <code>ToolFactory.java</code> to fix Stage 1 grading feedback.<br/>"
        "• <i>CLI Argument Parsing:</i> Updated <code>ApplicationFacade.java</code> to route <code>--mode=cli</code> directly and prevent HeadlessException."
    )
    story.append(Paragraph(changes_text, body_style))

    # Section 5: AI Tools Log
    story.append(Paragraph("5. AI Tools and Collaboration Log", h1_style))
    ai_headers = [Paragraph("Tool / Platform", table_cell_bold), Paragraph("LLM Model", table_cell_bold), Paragraph("Development Tasks", table_cell_bold), Paragraph("Token Cost", table_cell_bold), Paragraph("Money Cost", table_cell_bold)]
    ai_data = [
        ai_headers,
        [Paragraph("OpenAI API / ChatGPT", table_cell), Paragraph("GPT-3.5-Turbo / GPT-4o", table_cell), Paragraph("Architecture refactoring, ReAct logic, tool stubs", table_cell), Paragraph("~45,000 tokens", table_cell), Paragraph("$0.09 USD", table_cell)],
        [Paragraph("GitHub Copilot", table_cell), Paragraph("GPT-4o-mini", table_cell), Paragraph("Boilerplate code generation & Swing UI components", table_cell), Paragraph("Included in sub", table_cell), Paragraph("$0.00 USD", table_cell)]
    ]
    t_ai = Table(ai_data, colWidths=[105, 105, 170, 80, 80])
    t_ai.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#EAECEE')),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#BDC3C7')),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('TOPPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t_ai)

    # Section 6: Weekly Commits Summary
    story.append(Paragraph("6. Development and Verification Summary", h1_style))
    commit_headers = [Paragraph("Reporting Period", table_cell_bold), Paragraph("Date Range", table_cell_bold), Paragraph("Commit Count", table_cell_bold), Paragraph("Key Commit Activities & Hashes", table_cell_bold)]
    commit_data = [
        commit_headers,
        [Paragraph("Period 1", table_cell), Paragraph("Oct 20 - Oct 26, 2026", table_cell), Paragraph("5+ commits", table_cell), Paragraph("Initial repo setup, .gitignore, Stage 1 design docs", table_cell)],
        [Paragraph("Period 2", table_cell), Paragraph("Oct 27 - Nov 02, 2026", table_cell), Paragraph("5+ commits", table_cell), Paragraph("Added .env.example, agent_core, ApplicationFacade, DataManager", table_cell)],
        [Paragraph("Period 3", table_cell), Paragraph("Nov 03 - Nov 09, 2026", table_cell), Paragraph("5+ commits", table_cell), Paragraph("Added 10 tool classes, LLMClient, fixed CLI routing (442306a)", table_cell)]
    ]
    t_commit = Table(commit_data, colWidths=[80, 110, 80, 270])
    t_commit.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#EAECEE')),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#BDC3C7')),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 3),
        ('TOPPADDING', (0,0), (-1,-1), 3),
    ]))
    story.append(t_commit)

    # Section 7: Demonstration Index
    story.append(Paragraph("7. Feature Demonstration Video Index", h1_style))
    demo_headers = [Paragraph("Feature ID & Name", table_cell_bold), Paragraph("Interface Demonstrated", table_cell_bold), Paragraph("Video Link", table_cell_bold)]
    demo_data = [demo_headers]
    for i in range(1, 11):
        demo_data.append([
            Paragraph(f"F{i:02d}: {features_info[i-1][0].split(': ')[1]}", table_cell),
            Paragraph("CLI & GUI Modes", table_cell),
            Paragraph("https://drive.google.com/file/d/1e_SROuEhsqcdwOpftfZNulXx_KkDH23X/view?usp=sharing", table_cell)
        ])
    t_demo = Table(demo_data, colWidths=[150, 100, 290])
    t_demo.setStyle(TableStyle([
        ('BACKGROUND', (0,0), (-1,0), colors.HexColor('#EAECEE')),
        ('GRID', (0,0), (-1,-1), 0.5, colors.HexColor('#BDC3C7')),
        ('VALIGN', (0,0), (-1,-1), 'MIDDLE'),
        ('BOTTOMPADDING', (0,0), (-1,-1), 2.5),
        ('TOPPADDING', (0,0), (-1,-1), 2.5),
    ]))
    story.append(t_demo)

    doc.build(story)
    print("Successfully generated stage2-report.pdf")

if __name__ == '__main__':
    build_pdf()