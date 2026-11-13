#!/usr/bin/env python3
"""
Sync Architecture Documentation from docs/ to GitHub Wiki.

GitHub Wiki stores documentation in a dedicated Git repository:
  git@github.com:<owner>/<repo>.wiki.git

This script:
1. Validates connectivity with the GitHub Wiki Git repository.
2. Formats and flattens docs from `docs/` for GitHub Wiki:
   - docs/README.md -> Home.md
   - docs/arc42/ -> Arc42-*.md
   - docs/c4-model/ -> C4-*.md
   - docs/adr/ -> ADR-*.md
3. Rewrites internal markdown links between documents to match Wiki page names.
4. Generates a structured `_Sidebar.md` navigation menu and `_Footer.md`.
5. Commits and pushes the changes to GitHub Wiki.
"""

import os
import sys
import subprocess
import shutil
import re
import posixpath
import tempfile

def run_cmd(cmd, cwd=None, capture=True, check=True):
    res = subprocess.run(cmd, cwd=cwd, text=True, capture_output=capture)
    if check and res.returncode != 0:
        raise RuntimeError(f"Command failed (exit {res.returncode}): {' '.join(cmd)}\n{res.stderr}\n{res.stdout}")
    return res

def get_git_remote_url(repo_root):
    res = run_cmd(['git', 'config', '--get', 'remote.origin.url'], cwd=repo_root)
    url = res.stdout.strip()
    if not url:
        raise ValueError("Could not determine git origin URL.")
    return url

def get_wiki_remote_url(origin_url):
    # Convert git@github.com:owner/repo.git -> git@github.com:owner/repo.wiki.git
    # Convert https://github.com/owner/repo.git -> https://github.com/owner/repo.wiki.git
    if origin_url.endswith('.wiki.git'):
        return origin_url
    if origin_url.endswith('.git'):
        return origin_url[:-4] + '.wiki.git'
    return origin_url + '.wiki.git'

def check_wiki_initialized(wiki_url):
    res = run_cmd(['git', 'ls-remote', wiki_url], capture=True, check=False)
    return res.returncode == 0

def build_wiki_pages(docs_dir, target_dir):
    file_map = {
        'README.md': 'Home.md',
        'arc42/index.md': 'Arc42-Overview.md',
        'arc42/01_introduction_and_goals.md': 'Arc42-01-Introduction-and-Goals.md',
        'arc42/02_architecture_constraints.md': 'Arc42-02-Architecture-Constraints.md',
        'arc42/03_context_and_scope.md': 'Arc42-03-Context-and-Scope.md',
        'arc42/04_solution_strategy.md': 'Arc42-04-Solution-Strategy.md',
        'arc42/05_building_block_view.md': 'Arc42-05-Building-Block-View.md',
        'arc42/06_runtime_view.md': 'Arc42-06-Runtime-View.md',
        'arc42/07_deployment_view.md': 'Arc42-07-Deployment-View.md',
        'arc42/08_crosscutting_concepts.md': 'Arc42-08-Crosscutting-Concepts.md',
        'arc42/09_architectural_decisions.md': 'Arc42-09-Architectural-Decisions.md',
        'arc42/10_quality_requirements.md': 'Arc42-10-Quality-Requirements.md',
        'arc42/11_risks_and_technical_debt.md': 'Arc42-11-Risks-and-Technical-Debt.md',
        'arc42/12_glossary.md': 'Arc42-12-Glossary.md',
        'c4-model/README.md': 'C4-Model-Overview.md',
        'c4-model/01-system-context.md': 'C4-01-System-Context.md',
        'c4-model/02-container-view.md': 'C4-02-Container-View.md',
        'c4-model/03-component-view.md': 'C4-03-Component-View.md',
        'c4-model/04-code-and-data-model.md': 'C4-04-Code-and-Data-Model.md',
        'adr/README.md': 'ADR-Overview.md',
        'adr/0001-record-architecture-decisions.md': 'ADR-0001-Record-Architecture-Decisions.md',
        'adr/0002-decoupled-spa-and-rest-api.md': 'ADR-0002-Decoupled-SPA-and-REST-API.md',
        'adr/0003-spring-boot-layered-backend.md': 'ADR-0003-Spring-Boot-Layered-Backend.md',
        'adr/0004-in-memory-h2-database.md': 'ADR-0004-In-Memory-H2-Database.md',
        'adr/0005-nginx-reverse-proxy-container.md': 'ADR-0005-Nginx-Reverse-Proxy-Container.md',
        'adr/0006-react-context-state-management.md': 'ADR-0006-React-Context-State-Management.md',
        'adr/0007-dto-and-domain-entity-separation.md': 'ADR-0007-DTO-and-Domain-Entity-Separation.md',
    }

    link_pattern = re.compile(r'\[([^\]]+)\]\(([^)]+)\)')

    def rewrite_links(content, src_rel):
        src_dir = os.path.dirname(src_rel)
        def replace_match(m):
            text = m.group(1)
            url = m.group(2)
            if url.startswith(('http://', 'https://', '#', 'mailto:')):
                return m.group(0)
            url_base, *anchor = url.split('#', 1)
            anchor_part = f'#{anchor[0]}' if anchor else ''
            if not url_base:
                return m.group(0)
            target_path = posixpath.normpath(posixpath.join(src_dir, url_base))
            if target_path in file_map:
                wiki_slug = file_map[target_path].replace('.md', '') + anchor_part
                return f'[{text}]({wiki_slug})'
            return m.group(0)
        return link_pattern.sub(replace_match, content)

    for src_rel, target_filename in file_map.items():
        src_path = os.path.join(docs_dir, src_rel)
        if not os.path.exists(src_path):
            print(f"Warning: source file {src_path} not found, skipping.")
            continue
        with open(src_path, 'r', encoding='utf-8') as f:
            content = f.read()
        converted = rewrite_links(content, src_rel)
        target_path = os.path.join(target_dir, target_filename)
        with open(target_path, 'w', encoding='utf-8') as f:
            f.write(converted)

    sidebar_content = '''### 🧭 Navigation
* [🏠 Architecture Home](Home)

### 🏛️ arc42 Documentation
* [arc42 Overview](Arc42-Overview)
* [01. Introduction & Goals](Arc42-01-Introduction-and-Goals)
* [02. Architecture Constraints](Arc42-02-Architecture-Constraints)
* [03. Context & Scope](Arc42-03-Context-and-Scope)
* [04. Solution Strategy](Arc42-04-Solution-Strategy)
* [05. Building Block View](Arc42-05-Building-Block-View)
* [06. Runtime View](Arc42-06-Runtime-View)
* [07. Deployment View](Arc42-07-Deployment-View)
* [08. Crosscutting Concepts](Arc42-08-Crosscutting-Concepts)
* [09. Architectural Decisions](Arc42-09-Architectural-Decisions)
* [10. Quality Requirements](Arc42-10-Quality-Requirements)
* [11. Risks & Technical Debt](Arc42-11-Risks-and-Technical-Debt)
* [12. Glossary](Arc42-12-Glossary)

### 📐 C4 Model Visualizations
* [C4 Overview](C4-Model-Overview)
* [Level 1: System Context](C4-01-System-Context)
* [Level 2: Container View](C4-02-Container-View)
* [Level 3: Component View](C4-03-Component-View)
* [Level 4: Code & Data Model](C4-04-Code-and-Data-Model)

### 📋 Architecture Decisions (ADR)
* [ADR Overview & Matrix](ADR-Overview)
* [ADR-0001: Record Architecture Decisions](ADR-0001-Record-Architecture-Decisions)
* [ADR-0002: Decoupled SPA & REST API](ADR-0002-Decoupled-SPA-and-REST-API)
* [ADR-0003: Spring Boot Layered Backend](ADR-0003-Spring-Boot-Layered-Backend)
* [ADR-0004: In-Memory H2 Database](ADR-0004-In-Memory-H2-Database)
* [ADR-0005: Nginx Reverse Proxy Container](ADR-0005-Nginx-Reverse-Proxy-Container)
* [ADR-0006: React Context State Management](ADR-0006-React-Context-State-Management)
* [ADR-0007: DTO & Domain Entity Separation](ADR-0007-DTO-and-Domain-Entity-Separation)
'''

    footer_content = '''---
*Task Management Application Architecture Wiki | Generated from [TaskApp](https://github.com/arman-nqz/TaskApp)*
'''

    with open(os.path.join(target_dir, '_Sidebar.md'), 'w', encoding='utf-8') as f:
        f.write(sidebar_content)

    with open(os.path.join(target_dir, '_Footer.md'), 'w', encoding='utf-8') as f:
        f.write(footer_content)

    print(f"Generated {len(file_map) + 2} wiki pages in {target_dir}")

def sync_wiki():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    repo_root = os.path.abspath(os.path.join(script_dir, '..'))
    docs_dir = os.path.join(repo_root, 'docs')

    if not os.path.isdir(docs_dir):
        print(f"Error: {docs_dir} does not exist.")
        sys.exit(1)

    origin_url = get_git_remote_url(repo_root)
    wiki_url = get_wiki_remote_url(origin_url)
    print(f"Project repository remote: {origin_url}")
    print(f"Wiki repository remote:   {wiki_url}")

    if not check_wiki_initialized(wiki_url):
        print("\n" + "=" * 70)
        print("⚠️  GITHUB WIKI IS NOT INITIALIZED YET")
        print("=" * 70)
        print("GitHub does not provision the Git repository for a Wiki until the")
        print("very first wiki page is created through GitHub's web interface.")
        print("\nPlease follow these simple steps to enable it:")
        print(f" 1. Open your browser and navigate to:\n    https://github.com/arman-nqz/TaskApp/wiki")
        print(" 2. If 'Wikis' is disabled, enable it under Repository Settings -> Features.")
        print(" 3. Click 'Create the first page'.")
        print(" 4. Click 'Save page' at the bottom of the page.")
        print("\nOnce that initial page is saved on GitHub, rerun this script:")
        print("    python3 scripts/sync_wiki.py")
        print("=" * 70)
        sys.exit(1)

    print("\nWiki repository detected! Cloning wiki repository...")
    with tempfile.TemporaryDirectory() as temp_dir:
        run_cmd(['git', 'clone', wiki_url, temp_dir])
        
        # Remove all existing markdown files except .git
        for item in os.listdir(temp_dir):
            if item == '.git':
                continue
            item_path = os.path.join(temp_dir, item)
            if os.path.isfile(item_path) or os.path.islink(item_path):
                os.remove(item_path)
            elif os.path.isdir(item_path):
                shutil.rmtree(item_path)

        # Build and copy wiki pages
        build_wiki_pages(docs_dir, temp_dir)

        # Commit and push
        run_cmd(['git', 'add', '-A'], cwd=temp_dir)
        status_res = run_cmd(['git', 'status', '--porcelain'], cwd=temp_dir)
        if not status_res.stdout.strip():
            print("Wiki is already up to date. Nothing to commit.")
            return

        run_cmd(['git', 'commit', '-m', 'docs: sync architecture documentation from docs/ to wiki'], cwd=temp_dir)
        print("Pushing wiki changes to GitHub...")
        run_cmd(['git', 'push', 'origin', 'HEAD'], cwd=temp_dir)
        print("\n✅ Successfully updated GitHub Wiki!")
        print(f"View it online at: https://github.com/arman-nqz/TaskApp/wiki")

if __name__ == '__main__':
    sync_wiki()
